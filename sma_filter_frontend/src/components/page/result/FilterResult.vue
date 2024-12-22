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
    <h1>筛选结果</h1>

    <el-tabs v-model="activeTab">
        <el-tab-pane name="singleFilter">
            <template #label>
                <p style="font-size: 18px; margin: 0">单病例筛选</p>
            </template>
            <h4>共 {{ dat.length }} 条记录</h4>
            <div style="margin-left: 20px; height: calc(100vh - 350px);overflow-y: scroll; margin-bottom: 20px">

                <el-table :data="dat" v-loading="!isLoaded" element-loading-text="读取中..." empty-text="未筛出病例"
                          style="font-size: 16px">
                    <el-table-column prop="filename" label="文件" align="center" min-width="200px">
                        <template #default="{ row }">
                            <div v-html="row.fileName"></div>
                        </template>

                    </el-table-column>
                    <el-table-column prop="age" label="年龄" align="center" width="100px">
                        <template #default="{ row }">
                            <div v-html="row.age"></div>
                        </template>

                    </el-table-column>
                    <el-table-column prop="desc" label="描述" align="center" min-width="400px">
                        <template #default="{ row }">
                            <div v-for="content of row.keywords" v-html="content" style="margin: 5px"></div>
                        </template>

                    </el-table-column>
                    <el-table-column prop="open" label="操作" align="center" width="150px" fixed="right">
                        <template #default="{ row }">
                            <el-button @click="openFile(row.filePath)">打开文件</el-button>
                            <!--    <a :href="row.filePath">打开</a>-->
                        </template>

                    </el-table-column>
                </el-table>
            </div>

            <el-button type="success" @click="save(false)" style="float: bottom" :loading="saving">一键保存</el-button>

        </el-tab-pane>
        <el-tab-pane name="chartFilter">
            <template #label>
                <p style="font-size: 18px; margin: 0">表格筛选</p>
            </template>




            <h4>共 {{ chartResults.length }} 个文件， {{ chartEntryCount }} 条记录</h4>
            <div style="margin-left: 20px; height: calc(100vh - 350px);overflow-y: scroll; margin-bottom: 20px">
                <div v-for="chartResult of chartResults" class="group">
                    <el-card v-for="sheetResult of chartResult.sheetResults" style="text-align: left; width: calc(100% - 10px); margin: 20px 10px 20px 0">
                        <template #header>
                            <span style="font-size: 20px; font-weight: bolder; color: #555;"> {{ chartResult.fileName }} - {{ sheetResult.sheetName }} </span>
                        </template>
                        <el-table :data="sheetResult.values" v-loading="!isLoaded" element-loading-text="读取中..." empty-text="未筛出病例"
                                  style="font-size: 16px">
                            <el-table-column v-for="(t, index) of sheetResult.columnNames" :prop="t" :label="t" align="center" :width="sheetResult.columnSizes[index]/sheetResult.totalWidth*cardWidth">
                                <template #default="{ row }">
                                    <div v-html="row[index]"></div>
                                </template>

                            </el-table-column>
                        </el-table>
                    </el-card>
                </div>
            </div>
            <el-button type="success" @click="save(true)" style="float: bottom" :loading="saving">一键导出</el-button>


        </el-tab-pane>
    </el-tabs>


</template>

<script>
const { ipcRenderer } = window.require('electron')
export default {
    name: 'FilterResult',
    data ()
    {
        return {
            result: [],
            title: [],
            dat: [],
            saving: false,
            isLoaded: false,
            activeTab: 'singleFilter',
            chartEntryCount: 0,
            cardWidth: 500,
            chartResults: [
                {
                    filePath: 'path',
                    fileName: 'a.xlsx',
                    sheetResults: [
                        {
                            sheetName: 'sheet1',
                            columnNames: ['年龄', '结论'],
                            columnSizes: [2, 7],
                            values: [
                                ['14', '症状A, 症状B'],
                                ['20', '症状C, 症状D'],
                            ]
                        },
                        {
                            sheetName: 'sheet2',
                            columnNames: ['年龄', '结论'],
                            columnSizes: [2, 20],
                            values: [
                                ['24', '症A, 症状B'],
                                ['30', '症C, 症状D'],
                            ]
                        }
                    ]
                },
                {
                    filePath: 'path',
                    fileName: 'b.xlsx',
                    sheetResults: [
                        {
                            sheetName: 'Sheet 111',
                            columnNames: ['年龄', '提示'],
                            columnSizes: [2, 6],
                            values: [
                                ['34', '<span style="color: orange">症状A</span>, 状B'],
                                ['40', '<span style="color: red">症状C</span>, 状D'],
                            ]
                        },
                        {
                            sheetName: 'Sheet 2',
                            columnNames: ['年龄', '提示'],
                            columnSizes: [2, 4],
                            values: [
                                ['44', '症A, B'],
                                ['50', '症C, D'],
                            ]
                        }
                    ]
                }
            ]
        }
    },
    mounted ()
    {
        let _this = this
        _this.isLoaded = false
        _this.$axios.get(_this.$backend + '/getSingleResults').then(resp =>
        {
            if (resp.status === 200)
            {
                _this.dat = resp.data
                // if (resp.data.length == 0)
                // {
                //     _this.activeTab = 'chartFilter'
                // }
                for (let dat of _this.dat)
                {
                    if (dat.age === -1)
                    {
                        dat.age = '<span style=\'color:blue\'>未知</span>'
                    }
                }
                _this.$nextTick(function ()
                {
                    _this.isLoaded = true
                })
            }
        })
        _this.$axios.get(_this.$backend + '/getChartResults').then(resp => {
            if (resp.status === 200)
            {
                console.log(resp.data)
                _this.chartEntryCount = 0
                _this.chartResults = resp.data
                for (const chart of resp.data)
                {
                    for (const sheet of chart.sheetResults)
                    {
                        _this.chartEntryCount += sheet.values.length
                        sheet.totalWidth = 0;
                        for (const column of sheet.columnSizes)
                        {
                            sheet.totalWidth += column
                        }
                        _this.cardWidth = (window.innerWidth - 250) * 0.9
                    }
                }
            }
        })

        window.addEventListener('resize', () => {
            _this.cardWidth = (window.innerWidth - 250) * 0.9
        })

    },
    methods: {
        async save (isExcel)
        {
            let time = new Date()
            let fileName = '表格筛选导出' + time.getFullYear() + '-' + (time.getMonth()+1).toString().padStart(2, '0') + '-' + time.getDate().toString().padStart(2, '0') + '-' + time.getHours().toString().padStart(2, '0') + '-' + time.getMinutes().toString().padStart(2, '0') + '.xlsx'
            let _this = this
            var res = await ipcRenderer.invoke('openDialog', 'save')
            console.log(res)
            this.saving = true
            // ipcMain.handle('selectedItem', (event, files) => {
            ipcRenderer.on('selectedItemSave', (event, files) =>
            {
                var lastIdx = files[0].lastIndexOf('/')
                if (lastIdx == -1)
                {
                    lastIdx = files[0].lastIndexOf('\\')
                }
                if (lastIdx != -1)
                {
                    if (isExcel)
                    {
                        _this.$axios.post(this.$backend + '/saveChartCases', {
                            name: _this.$store.state.centerName,
                            path: files[0] + '/' + fileName,
                        }).then(resp =>
                        {
                            if (resp.status === 200)
                            {
                                _this.$message.success('导出成功')
                            }
                            _this.saving = false
                        }).catch(err =>
                        {
                            _this.saving = false
                            _this.$message.error("导出失败，请尝试更换文件夹")
                        })
                    }
                    else
                    {
                        _this.$axios.post(this.$backend + '/saveSingleCases', {
                            name: _this.$store.state.centerName,
                            path: files[0],
                        }).then(resp =>
                        {
                            if (resp.status === 200)
                            {
                                _this.$message.success('保存成功')
                            }
                            _this.saving = false
                        }).catch(err =>
                        {
                            _this.saving = false
                            _this.$message.error("保存失败，请尝试更换文件夹")

                        })
                    }
                }
            })
        },
        openFile (path)
        {
            ipcRenderer.invoke('openFile', path)

        }
    }
}
</script>

<style scoped>

/*.redText*/
/*{*/
/*    color: red!important;*/
/*    display: inline;*/
/*}*/


</style>
