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
    <center-import-dialog ref="centerImportDialog" @ret="submit"/>
<!--    <el-button @click="showImportDialog" style="margin: 20px;left: 0;display: flex">返回</el-button>-->
    <div style="width: 90%; text-align: right">
        <el-icon id="closeIcon" style="font-size: 28px; display: inline-block;">
            <Close @click="goBack"/>
        </el-icon>
    </div>
    <h1>中心列表</h1>
    <div style="display: inline-block; width: 80%">
        <div style="text-align: start">
            <el-button type="primary" @click="showImportDialog" style="margin: 20px;left: 0;display: inline">导入</el-button>
<!--            <el-button v-if="importPath == ''" type="primary" @click="showImportDialog" style="margin: 20px;left: 0;display: inline">导入</el-button>-->
<!--            <el-input v-if="importPath != ''" v-model="importPath" style="width: 50%"/>-->
<!--            <el-button v-if="importPath != ''" type="primary" plain @click="showImportDialog" style="margin: 20px;left: 0;display: inline">重新选择</el-button>-->
<!--            <el-button v-if="importPath != ''" type="primary" @click="importRow" style="margin: 20px;left: 0;display: inline">确认导入</el-button>-->
        </div>
        <el-table :data="centerList" empty-text="无数据。请导入" style="font-size: 16px">
            <el-table-column prop="name" label="名称" align="center">
                <template #default="{row}">
                    <el-input v-model="row.name" style="font-size: 16px" @change="changeCenterName"/>
                </template>
            </el-table-column>
            <el-table-column prop="operation" label="操作" width="200px" align="center">
                <template #default="{row}">
                    <el-button @click="removeRow(row)" type="danger" style="font-size: 16px">
                        <el-icon class="sideButtonIcon">
                            <Delete/>
                        </el-icon>
                        删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
    </div>
    <div>
        <el-button type="primary" @click="loadList" style="margin: 20px; display: inline-block" plain
                   :disabled="!changed">放弃更改
        </el-button>
        <el-button type="primary" @click="save" style="margin: 20px; display: inline-block" :disabled="!changed">保存
        </el-button>

    </div>
</template>

<script>
import CenterImportDialog from '@/components/page/home/CenterImportDialog'
export default {
    name: 'CenterSetting',
    components: { CenterImportDialog },
    data(){
        return {
            centerList: [
                {
                    id: 1,
                    name: "a"
                }
            ],
            changed: false,
        }
    },
    mounted ()
    {
        this.loadList()
    },
    methods: {
        loadList()
        {
            this.changed = false;
            this.centerList = []
            let _this = this
            _this.$axios.get(_this.$backend + "/getCenterList").then(resp => {
                if (resp.status === 200)
                {
                    _this.centerList = resp.data
                }
            })
        },
        removeRow(row)
        {
            let index = this.centerList.indexOf(row)
            this.centerList.splice(index, 1)
            this.changed = true
        },
        showImportDialog()
        {
            this.$refs.centerImportDialog.visible = true
        },
        changeCenterName()
        {
            this.changed = true
        },
        save()
        {
            let _this = this
            _this.$axios.put(_this.$backend + "/setCenterList", _this.centerList).then(resp => {
                if (resp.status === 200)
                {
                    _this.$message.success("保存成功")
                    _this.changed = false
                    _this.$store.state.centerName = ''
                }
            }).catch(err => {
                // console.log(err.response)
                if (err.response.status === 400)
                {
                    _this.$message.error(err.response.data)
                }
            })
        },
        submit(path)
        {
            let _this = this
            if (path != null && path != '')
            {
                _this.$axios.post(_this.$backend + "/importCenterList", {
                    path: path
                }).then(resp => {
                    if (resp.status === 200)
                    {
                        _this.$message.success("导入成功！")
                        _this.loadList()
                    }
                })
                // query backend to import
            }
        },
        goBack()
        {
            this.$router.push("/CenterSelect")
        }

    }
}
</script>

<style scoped>
#closeIcon
{
    color: #777777;
    cursor: pointer;
}

#closeIcon:hover
{
    color: #aaaaaa
}
</style>
