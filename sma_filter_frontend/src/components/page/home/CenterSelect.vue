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
    <h1>SMA Filter</h1>
    <div style="height: 100px;"></div>
    <el-select v-model="selectedCenter" placeholder="请选择对应中心" @change="setCenter" :loading="isLoading" >
        <el-option v-for="center in centerList" :key="center" :label="center" :value="center"/>
    </el-select>
    <el-button style="margin: 20px" type="primary" @click="openCenterSetting">
        <el-icon>
            <setting/>
        </el-icon>
        设置
    </el-button>
    <div style="margin: 10px; width: 100%"></div>
<!--    <el-button type="primary" @click="addCenter">添加新的中心</el-button>-->
    <div style="bottom: 20px; right: 20px;position: absolute">
        <p>
            复旦大学类脑智能研究院DMIIP实验室
            <br>
            复旦附属华山医院罕见病中心
        </p>
    </div>
</template>

<script>
import {ElLoading} from 'element-plus'
const {ipcRenderer} = window.require('electron')
const electron = window.require('electron')

export default {
    name: 'CenterSelect',
    data ()
    {
        return {
            centerList: [],
            selectedCenter: "",
            isLoading: true,
            intervalId: null,
            backendPath: '',
            loading: null
        }
    },
    mounted ()
    {


        // console.log(__dirname);
        // var cluster = window.require('child_process')
        // // console.log(__dirname + '..\\..\\..\\..\\..\\..\\dist_electron\\java\\bin\\java.exe -version')
        // // cluster.exec('..\\..\\..\\..\\..\\..\\dist_electron\\java\\bin\\java.exe -version', (err) => {
        // //     console.log(err)
        // // })
        // cluster.exec('.\\java\\bin\\java.exe -version', (err, stdout) => {
        //     console.log(err)
        //     console.log(stdout)
        // })
        // cluster.exec('.\\java\\bin\\java.exe -jar SMAFilterBackend-1.0-SNAPSHOT.jar', (err, stdout) => {
        //     // console.log(err)
        //     // console.log(stdout)
        // })

        // for test only

        let _this = this





        // this.$message.info("hi")
        // this.$message.info(app.getPath('appData'))
        _this.selectedCenter = _this.$store.state.centerName
        if (!_this.$store.state.backendStarted)
        {
            _this.isLoading = true
            _this.loading = ElLoading.service({
                lock: true,
                text: '准备中...',
                background: 'rgba(0, 0, 0, 0.7)'
            })

            var res = ipcRenderer.invoke("getAppPath")
            ipcRenderer.on('appPath', (event, path) => {
                _this.backendPath = path
                _this.tryConnectBackend()
            })
        }
        else
        {
            _this.loadCenterList()
        }
    },
    methods: {
        tryConnectBackend()
        {
            var _this = this
            _this.intervalId = setInterval(function()
            {
                // _this.$message.info(path)
                _this.$axios.post(_this.$backend + "/setBackendPath", {
                    path: _this.backendPath
                }).then(resp => {
                    if (resp.status === 200)
                    {
                        clearInterval(_this.intervalId)
                        _this.loadCenterList()
                        _this.$store.dispatch('startBackend')
                        _this.loading.close()
                    }
                }).catch(err => {

                })
            }, 1000)
        },
        setCenter()
        {
            // console.log(this.selectedCenter)
            if (this.selectedCenter != '')
            {
                this.$store.dispatch('updateCenterName', this.selectedCenter)
            }
        },
        openCenterSetting()
        {
            this.$router.push("/CenterSetting")
        },
        loadCenterList()
        {
            let _this = this
            _this.$axios.get(_this.$backend + "/getCenterList")
                .then(resp => {
                    if (resp.status === 200)
                    {
                        _this.centerList = []
                        for (let center of resp.data)
                        {
                            _this.centerList.push(center.name)
                        }
                        // _this.centerList = resp.data
                        _this.isLoading = false;
                    }
                })
                .catch(err => {
                    _this.$message.error("读取失败")
                    // this.centerList = ["华山总院", "华山西院"]
                    // this.$emit('selectFilterJson', js)
                    // _this.$store.dispatch('updateCenterName', '华山总院')
                })
        }
    }
}
</script>

<style scoped>

</style>
