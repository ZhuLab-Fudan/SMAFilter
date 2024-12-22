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
    <el-drawer
        direction="btt"
        :model-value="visible"
        :before-close="handleClose"
        :close-on-press-escape="true"
        :close-on-click-modal="true"
        size="40%"
    >
        <template #title>
            <span style="font-size: 20px; color: #555; font-weight: bolder;">导入文件</span>
        </template>
        <el-input v-model="path" style="display: inline-block; width: calc(90% - 150px)"/>
        <input v-show="false" ref="pathInput" type="file" accept="application/json" @change="fileChange"/>
        <el-button type="primary" @click="chooseFile" style="margin: 20px; width: 100px">选择文件</el-button>
        <div style="margin: 20px">
            <el-button @click="close" type="primary" plain>取消</el-button>
            <el-button @click="submit" type="primary">导入</el-button>
        </div>
    </el-drawer>
</template>

<script>
export default {
    name: 'CenterImportDialog',
    emits: ['ret'],
    data()
    {
        return {
            visible: false,
            path: ''
        }
    },
    methods: {
        show()
        {
            this.visible = true
        },
        chooseFile()
        {
            let _this = this
            _this.$refs.pathInput.dispatchEvent(new MouseEvent('click'))
        },
        fileChange()
        {
            this.path = this.$refs.pathInput.files[0].path
        },
        submit()
        {
            this.visible = false;
            this.$emit('ret', this.path);
        },
        close()
        {
            this.visible = false;
            this.$emit('ret', null);
        },
        handleClose(done)
        {
            this.close()
            done()
        }
    }
}
</script>

<style scoped>

</style>
