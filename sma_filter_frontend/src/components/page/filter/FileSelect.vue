<!--
Copyright [2024] [Institute of Science and Technology for Brain-Inspired Intelligence, Fudan University]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->
<template>
    <h1>请选择病例所在文件夹</h1>
    <div style="height: 50px;">

    </div>
    <div style="margin-left: 20px; height: calc(100% - 200px)">


<!--        <el-card style="width: 80%; margin-left: 10%;">-->
            <el-form id="pathForm" :model="form" ref="form">
                <el-form-item label="病例文件夹" :inline="true" :rules="{
                    required: true, message: '请选择病例文件夹', trigger: 'blur'
                }">
                    <el-input v-model="form.outputPath" autocomplete="off" style="width: 60%"/>
                    <el-button type="primary" style="margin-left: 5%" @click="upload()" >选择文件夹</el-button>
                </el-form-item>
                <el-form-item label="">
                    <el-checkbox v-model="form.processSubFolder" size="large" border>
                        <span style="font-size: 18px">处理子文件夹</span>
                    </el-checkbox>
                </el-form-item>

            </el-form>


<!--            <el-button type="success" v-loading="executing" style="float: right;margin-bottom: 30px;margin-top: 40px;margin-right: 10px" @click="execute">开始筛选</el-button>-->
<!--        </el-card>-->

    </div>
    <el-button type="success" @click="execute" style="float: bottom">开始筛选</el-button>
</template>

<script>
// const {ipcRenderer} = require('electron')
const {ipcRenderer} = window.require('electron')
export default {
    name: 'FileSelect',
    data() {
        return {
            form: {
                output: '',
                outputPath: '',
                processSubFolder: true
            },
        }
    },
    methods: {
        async upload()
        {

            var res = await ipcRenderer.invoke("openDialog", "read")
            console.log(res)
            // ipcMain.handle('selectedItem', (event, files) => {
            ipcRenderer.on('selectedItemRead', (event, files) => {
                console.log(event)
                console.log(files)
                var lastIdx = files[0].lastIndexOf("/");
                if (lastIdx == -1)
                {
                    lastIdx = files[0].lastIndexOf("\\");
                }
                if (lastIdx != -1)
                {
                    this.form.outputPath = files[0]
                    this.form.output = files[0].substr(lastIdx + 1)
                }
            })
        },
        execute()
        {
            let _this = this
            this.$axios.post(this.$backend + "/startProcess", {
                name: _this.$store.state.centerName,
                path: _this.form.outputPath,
                processSubFolder: _this.form.processSubFolder
            })
            _this.$router.push("/Processing")
            //
            // // var shell = new window.ActiveXObject("WScript.Shell");
            // // console.log(shell)
            // // shell.exec("D:/Skyline_Final_1.exe")
            // var cluster = window.require('child_process')
            // this.executing = true
            // // var proc = cluster.exec(path.dirname(app.getPath('exe')) + "/res/Skyline_Final_1.exe", (err) => {
            // var proc = cluster.exec(".\\res\\Skyline_Final_1.exe", (err) => {
            //     console.log(err)
            //     // console.log(res)
            //     if (err != null)
            //     {
            //         var fileNotExist = err.toString().lastIndexOf("No such file or directory")
            //         var formatError = err.toString().lastIndexOf("KeyError");
            //         if (fileNotExist != -1)
            //         {
            //             this.$message.error("File not found")
            //         }
            //         else if (formatError != -1)
            //         {
            //             this.$message.error("File format error")
            //         }
            //         else
            //         {
            //             this.$message.error("Unknown error")
            //         }
            //     }
            //     else
            //     {
            //         this.$message.success("Done!")
            //     }
            //
            //     this.executing = false
            // })
            // proc.stdin.write(this.form.reportPath + "\n");
            // proc.stdin.write(this.form.dataPath + "\n");
            // proc.stdin.write(this.form.outputPath + "\n");

            // proc.

        },
    }
}
</script>

<style scoped>
#pathForm>>>.el-form-item__label
{
    font-size: 20px;
}
.el-button
{
    font-size: 16px;
}

#question:hover
{
    color: #0f3dac99!important;
}

#content
{
    font-size: 20px;
}


/*.el-checkbox__inner {*/
/*    width: 20px!important;*/
/*    height: 20px!important;*/
/*}*/

/*.el-checkbox__inner::after*/
/*{*/
/*    border: 3px solid #fff;*/
/*    border-left: 0;*/
/*    border-top: 0;*/
/*    left: 5px;*/
/*    top: 5px;*/
/*}*/

/*.checkbox*/
/*{*/
/*    zoom: 200%;*/
/*}*/
</style>
