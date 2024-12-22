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
const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
    transpileDependencies: true,
    lintOnSave: false,
    pluginOptions: {
        electronBuilder: {
            nodeIntegration: true,
            customFileProtocol: './',
            builderOptions: {
                //解决用户不能自己选择安装目录
                nsis: {
                    oneClick: false,
                    allowToChangeInstallationDirectory: true
                },
                "extraResources": [
                    // {
                    //     "from": "./dist_electron/application.json",
                    //     "to": "./application.json"
                    // },
                    // {
                    //     "from": "./dist_electron/result.json",
                    //     "to": "./result.json"
                    // },
                    {
                        "from": "./dist_electron/tessdata",
                        "to": "./tessdata"
                    },
                    {
                        "from": "./dist_electron/java",
                        "to": "./java"
                    },
                    {
                        "from": "./dist_electron/SMAFilterBackend-1.0-SNAPSHOT.jar",
                        "to": "./SMAFilterBackend-1.0-SNAPSHOT.jar"
                    }
                ]
            },
        }
    }
})
