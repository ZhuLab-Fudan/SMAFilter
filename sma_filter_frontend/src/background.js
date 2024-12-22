/*
 * Copyright [2024] [Institute of Science and Technology for Brain-Inspired Intelligence, Fudan University]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict'

import { app, protocol, BrowserWindow, shell, Menu } from 'electron'
import { createProtocol } from 'vue-cli-plugin-electron-builder/lib'
import installExtension, { VUEJS3_DEVTOOLS } from 'electron-devtools-installer'

const isDevelopment = process.env.NODE_ENV !== 'production'
const ipcMain = require('electron').ipcMain;
const dialog = require('electron').dialog;
const exec = require('child_process');
const {spawn} = require('child_process');
const path = require('path')

const fs = require('fs')
// const pipePath = '\\\\.\\pipe\\SMAFilter';

// Scheme must be registered before the app is ready
protocol.registerSchemesAsPrivileged([
    { scheme: 'app',
        privileges: {
            secure: true,
            standard: true
        }
    }
])


// function sendMessage(message)
// {
//     const writeStream = fs.createWriteStream(pipePath);
//     writeStream.write(message);
//     writeStream.end();
// }

async function createWindow () {
    // Create the browser window.
    const win = new BrowserWindow({
        width: 1280,
        height: 768,
        webPreferences: {

            // Use pluginOptions.nodeIntegration, leave this alone
            // See nklayman.github.io/vue-cli-plugin-electron-builder/guide/security.html#node-integration for more info
            nodeIntegration: process.env.ELECTRON_NODE_INTEGRATION,
            contextIsolation: !process.env.ELECTRON_NODE_INTEGRATION
        }
    })

    Menu.setApplicationMenu(null)

    if (process.env.WEBPACK_DEV_SERVER_URL) {
        // Load the url of the dev server if in development mode
        await win.loadURL(process.env.WEBPACK_DEV_SERVER_URL)
        if (!process.env.IS_TEST) win.webContents.openDevTools()
    } else {
        createProtocol('app')
        // Load the index.html when not in development
        win.loadURL('app://./index.html')
    }

    // exec('.\\java\\bin\\java.exe -version', (err, stdout, stderr) => {
    //     console.log(err)
    //     console.log(stdout)
    //     console.log(stderr)
    // }).catch(err => {
    //     console.log(err)
    // })
    // const absPath = path.resolve(__dirname, ".\\java\\bin\\java.exe")
    // console.log(absPath);

    // const absPath2 = path.resolve(__dirname, ".\\SMAFilterBackend-1.0-SNAPSHOT.jar")

    // const subprocess = spawn(absPath, ['-jar', absPath2]);
    // window.alert(__dirname)
    // dialog.showMessageBox({
    //     type: 'info',
    //     title: 'dirname',
    //     message: path.dirname(__dirname),
    //     buttons: ['OK']
    // })
    // dialog.showMessageBox({
    //     type: 'info',
    //     title: 'resourcePath',
    //     message: process.resourcesPath,
    //     buttons: ['OK']
    // })
    console.log(__dirname)
    // const subprocess = spawn(".\\java\\bin\\java.exe", ['-jar', ".\\SMAFilterBackend-1.0-SNAPSHOT.jar"], {cwd: __dirname.replace("\\app.asar", "")});
    const subprocess = spawn(".\\java\\bin\\java.exe", ['-jar', ".\\SMAFilterBackend-1.0-SNAPSHOT.jar"], {cwd: path.dirname(__dirname)});
    subprocess.stdout.on('data', (data) => {
        console.log(data.toString())
    })
    subprocess.stderr.on('data', (data) => {
        console.log(data.toString())
    })

    // alert(app.getPath('appData') + "\\SMA_Filter\\application.json")

    // sendMessage("Hi")



}

// Quit when all windows are closed.
app.on('window-all-closed', () => {
    // On macOS it is common for applications and their menu bar
    // to stay active until the user quits explicitly with Cmd + Q
    if (process.platform !== 'darwin') {
        app.quit()
    }
})

app.on('activate', () => {
    // On macOS it's common to re-create a window in the app when the
    // dock icon is clicked and there are no other windows open.
    if (BrowserWindow.getAllWindows().length === 0) createWindow()
})

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.on('ready', async () => {
    if (isDevelopment && !process.env.IS_TEST) {
        // Install Vue Devtools
        try {
            await installExtension(VUEJS3_DEVTOOLS)
        }
        catch (e) {
            console.error('Vue Devtools failed to install:', e.toString())
        }
    }
    createWindow()
    handleIPC()
})

// Exit cleanly on request from parent process in development mode.
if (isDevelopment) {
    if (process.platform === 'win32') {
        process.on('message', (data) => {
            if (data === 'graceful-exit') {
                app.quit()
            }
        })
    } else {
        process.on('SIGTERM', () => {
            app.quit()
        })
    }
}

function handleIPC()
{
    ipcMain.handle('openDialog', function(event, ...args) {
        // console.log("!")
        // console.log(args)
        // console.log(args[0])
        dialog.showOpenDialog({
            properties: ["openDirectory"]
        }).then(res => {
            // console.log(res)
            // console.log(event)
            if (res.filePaths.length > 0) {
                // for test only

                // ipcRenderer.send('selectedItem', res.filePaths);
                // console.log(ipcRenderer)
                if (args[0] == 'read')
                {
                    event.sender.send('selectedItemRead', res.filePaths);
                }
                else if (args[0] == 'save')
                {
                    event.sender.send('selectedItemSave', res.filePaths);
                }

                // return res.filePaths;
            }
            // else
            // {
            //     return 0
            // }
        })
    })

    ipcMain.handle('openFile', function(event, ...args){
        console.log(args);
        shell.openPath(args[0])
        // shell.openItem()
        // window.open(args)
        // fetch(args).then(response =>response.blob())
        // .then(blob => {
        //     const objectURL = URL.createObjectURL(blob)
        //     window.open(objectURL, "_blank")
        //     URL.revokeObjectURL(objectURL)
        //
        // })
    })

    ipcMain.handle('getAppPath', function(event, ...args)
    {
        event.sender.send('appPath', app.getPath('appData') + "\\SMA_Filter\\")
    })
}
