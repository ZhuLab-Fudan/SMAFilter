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
    <h1>筛选设置</h1>
    <!--    <filter-word-dialog ref="filterWordDialog" :visible="filterWordDialogVisible" :content="filterWordContent" @ret="submit"/>-->
    <filter-word-dialog ref="filterWordDialog" @ret="submit"/>
    <chart-filter-rule-dialog ref="chartFilterRuleDialog" @ret="submitChartFilter"/>

    <el-tabs v-model="activeTab">
        <el-tab-pane name="singleFilter">
            <template #label>
                <p style="font-size: 18px; margin: 0">单病例筛选</p>
            </template>

            <div style="width: 100%;display: inline-block">

                <el-button type="primary" @click="addRow" style="margin: 20px;left: 0;display: flex">添加条件</el-button>
                <el-table :data="dat" empty-text="无筛选规则" style="font-size: 16px;">
                    <el-table-column prop="age" label="年龄" align="center" width="370px">
                        <!--        <el-table-column v-for="(item, idx) in this.result[index]" :key="idx" :label="item"/>-->
                        <!--        <p v-for="(item, idx) in this.result[index]" :key="idx +'_' + index" >{{ item }}</p>-->
                        <!--        <template #default="scope">-->
                        <template #default="{ row }">
                            <!--                <div v-html="row.age"></div>-->
                            <el-select v-model="row.ageType" style="width: 130px;" @change="changeAge(row)">
                                <el-option label="大于" value="大于"/>
                                <el-option label="介于" value="介于"/>
                                <el-option label="小于" value="小于"/>
                                <el-option label="全年龄段" value="全年龄段"/>
                            </el-select>
                            <el-input-number v-if="row.ageType == '介于' || row.ageType == '大于'" v-model="row.minAge"
                                             :min=0
                                             style="width: 150px;margin: 10px" @change="changeAge(row)"/>
                            <el-input-number v-if="row.ageType == '小于'" v-model="row.maxAge" :min=0
                                             style="width: 150px;margin: 10px" @change="changeAge(row)"/>
                            <p v-if="row.ageType != '全年龄段'" style="display: inline;">岁</p>
                            <div v-if="row.ageType=='介于'">
                                <p style="display: inline-block; width:130px">和</p>
                                <el-input-number v-model="row.maxAge" :min=0 style="width: 150px;margin: 10px"
                                                 @change="changeAge(row)"/>
                                <p style="display: inline;">岁</p>
                            </div>
                        </template>

                        <!--            <p v-for="(item, idx) of scope.row[index]" :key="idx" >{{ item }} _ {{ scope.index }}</p>-->
                        <!--            <p v-for="(item, idx) of this.result[index]" :key="idx" >{{ item }} _ {{ scope.index }}</p>-->
                        <!--            <p>{{ scope.row[t] }}</p>-->
                        <!--            <div v-html="scope.row[t]"></div>-->
                        <!--            <p v-for="(item, idx) of scope.row[t] " :key="idx" >{{ item }} _ {{ t }} _ {{ idx }} _ {{ scope.row[t] }}</p>-->
                    </el-table-column>
                    <el-table-column prop="filterWord" label="筛选词" align="center" min-width="300px">
                        <template #default="{ row }">
                            <!--                <div v-html="row.filterWord"></div>-->
                            <div v-html="row.filterWordDesc"></div>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" align="center" width="250px">
                        <template #default="{ row }">
                            <el-button type="primary" @click="showFilterWordDialog(row)">修改筛选词</el-button>
                            <el-button type="danger" @click="removeRow(row)">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
        </el-tab-pane>
        <el-tab-pane name="chartFilter">
            <template #label>
                <p style="font-size: 18px; margin: 0">表格筛选</p>
            </template>
            <el-button type="primary" @click="addChartGroup" style="margin: 20px;left: 0;display: flex">添加一组条件
            </el-button>

            <div v-if="chartFilterSetting.length == 0"
                 style="font-size: 20px; color: #777; font-weight: bold; margin-bottom: 50px">尚未添加条件
            </div>

            <el-card v-for="(group, groupIndex) of chartFilterSetting" class="group"
                     style="margin: 20px 0; text-align: left">
                <template #header>
                    <span style="font-size: 20px; font-weight: bolder; color: #555;">第 {{ groupIndex + 1 }} 组条件</span>
                    <el-button @click="removeChartGroup(groupIndex)" style="float: right; padding: 0; margin: 0 10px"
                               type="text">删除这组条件
                    </el-button>
                    <el-button @click="showChartFilterRuleDialog(groupIndex, -1)"
                               style="float: right; padding: 0; margin: 0 10px" type="text">组内增加一个条件
                    </el-button>
                </template>
                <div v-if="group.rules.length === 0" style="color: #777; font-weight: bold">尚未添加条件</div>
                <div v-for="(rule, ruleIndex) in group.rules" class="rule" style="width: 100%">
                    <div v-if="ruleIndex === 0"
                         style="border-bottom: #ddd solid 1px; border-top: #ddd solid 1px;position: relative">
                        <div v-html="getChartFilterText(rule)"
                             style="display: inline-block; width: calc(100% - 200px); color: #555; margin: 10px; white-space: normal; line-height: 30px; font-size: 16px"/>
                        <div style="display: inline-block; width: 180px; position: absolute; top: calc(50% - 20px)">
                            <el-button @click="showChartFilterRuleDialog(groupIndex, ruleIndex)" type="primary"
                                       style="display: inline">修改
                            </el-button>
                            <el-button @click="removeChartRule(groupIndex, ruleIndex)" type="danger"
                                       style="display: inline">删除
                            </el-button>
                        </div>
                    </div>
                    <div v-if="ruleIndex > 0" style="border-bottom: #ddd solid 1px;position: relative">
                        <div v-html="getChartFilterText(rule)"
                             style="display: inline-block; width: calc(100% - 200px); color: #555; margin: 10px; white-space: normal; line-height: 30px; font-size: 16px"/>
                        <div style="display: inline-block; width: 180px;position: absolute; top: calc(50% - 20px)">
                            <el-button @click="showChartFilterRuleDialog(groupIndex, ruleIndex)" type="primary"
                                       style="display: inline">修改
                            </el-button>
                            <el-button @click="removeChartRule(groupIndex, ruleIndex)" type="danger"
                                       style="display: inline">删除
                            </el-button>
                        </div>
                    </div>
                </div>
            </el-card>
        </el-tab-pane>
    </el-tabs>

    <!--    <TreeNode :node="expressionTree" />-->
    <!--    <TreeNode/>-->
    <!--    <div style="height: 50px;"></div>-->


    <div>
        <el-button type="primary" @click="load" style="margin: 20px; display: inline-block" plain
                   :disabled="!changed">放弃更改
        </el-button>
        <el-button type="primary" @click="save" style="margin: 20px; display: inline-block" :disabled="!changed">保存
        </el-button>

    </div>


    <div style="text-align: start">
        <p>筛选词示例：</p>
        <li style="margin: 5px 0">X <span style="color: blue">AND</span> Y：需要同时出现 X 和 Y 才符合条件</li>
        <li style="margin: 5px 0">X <span style="color: blue">OR</span> Y：X 和 Y 只要其中一个出现就符合条件</li>
        <li style="margin: 5px 0"><span style="color: blue">NOT</span> X：不出现 X 才满足条件</li>
    </div>

</template>

<script>
import TreeNode from '@/components/TreeNode'
import FilterWordDialog from '@/components/page/setting/FilterWordDialog'
import ChartFilterRuleDialog from '@/components/page/setting/ChartFilterRuleDialog'

export default {
    name: 'FilterSetting',
    components: {
        ChartFilterRuleDialog,
        FilterWordDialog,
        TreeNode
    },
    data ()
    {
        return {
            dat: [
                {
                    age: {
                        min: 14,
                        max: 40,
                        type: '介于'
                    },
                    filterWord: 'abc AND def',
                    filterWordDesc: 'abc <span style=\'color: blue\'>AND</span> def'
                }
            ],
            labels: ['小于', '介于', '大于', '全年龄段'],
            columnSelectTypes: ['根据列的序号查找', '根据列的名称查找'],
            columnTypes: ['数字', '文本'],
            currentIndex: 0,
            filterWordDialogVisible: false,
            filterWordContent: '',
            changed: false,
            reportPatternList: {},
            activeTab: 'singleFilter',
            // activeTab: 'chartFilter',
            modifyGroupIndex: -1,    // which group to modify
            modifyRuleIndex: -1,     // in the group, which rule to modify. -1 means add at last
            chartFilterSetting: []
        }
    },
    mounted ()
    {
        this.load()
    },
    methods: {
        load ()
        {
            let _this = this
            _this.dat = []
            _this.chartFilterSetting = []
            _this.$axios.get(this.$backend + '/getFilterSettingByName/' + _this.$store.state.centerName)
                .then(resp =>
                {
                    if (resp.status === 200)
                    {
                        let obj = {}
                        for (const element of resp.data.filters)
                        {
                            _this.dat.push({
                                maxAge: element.maxAge,
                                minAge: element.minAge,
                                ageType: _this.labels[element.ageType],
                                filterWord: element.text,
                                filterWordDesc: _this.getDescText(element.text),
                            })
                        }
                        console.log(resp.data.chartFilters)
                        for (const chartFilter of resp.data.chartFilters)
                        {
                            let rules = []
                            for (const rule of chartFilter.rules)
                            {
                                console.log(rule)
                                let obj = {}
                                obj.columnSelectType = _this.columnSelectTypes[rule.columnSelectType]
                                obj.columnIndex = rule.columnIndex + 1
                                obj.columnName = rule.columnName
                                obj.columnType = _this.columnTypes[rule.columnType]
                                if (rule.numberPattern != null)
                                {
                                    obj.numberPattern = {
                                        min: rule.numberPattern.min,
                                        max: rule.numberPattern.max,
                                        type: _this.labels[rule.numberPattern.type]
                                    }
                                }
                                else
                                {
                                    obj.numberPattern = {min: 0, max: 100, type: '介于'}
                                }
                                obj.stringPattern = rule.stringPattern
                                rules.push(obj)
                            }
                            _this.chartFilterSetting.push({rules: rules})
                        }
                        _this.reportPatternList = resp.data.reportPatternList
                        _this.changed = false
                    }
                }).catch(err =>
            {

            })
        },
        showFilterWordDialog (row)
        {
            let _this = this
            // console.log(_this.filterWordDialogVisible)
            _this.filterWordDialogVisible = true
            // console.log(_this.filterWordDialogVisible)
            let index = _this.dat.indexOf(row)
            _this.currentIndex = index
            _this.filterWordContent = ''
            _this.filterWordContent = row.filterWord
            _this.$refs.filterWordDialog.show(row.filterWord)
        },
        showChartFilterRuleDialog (groupIndex, ruleIndex)
        {
            let _this = this
            _this.modifyGroupIndex = groupIndex
            _this.modifyRuleIndex = ruleIndex
            if (ruleIndex >= 0)
            {
                let rule = JSON.parse(JSON.stringify(_this.chartFilterSetting[groupIndex].rules[ruleIndex]))
                _this.$refs.chartFilterRuleDialog.show(rule)
            }
            else  // for add
            {
                _this.$refs.chartFilterRuleDialog.show({
                    columnSelectType: '根据列的序号查找',
                    columnIndex: 1,
                    columnType: '数字',
                    numberPattern: {
                        type: '介于',
                        max: 100,
                        min: 0
                    },
                    stringPattern: ''
                })
            }
        },
        removeChartGroup (groupIndex)
        {
            let _this = this
            _this.chartFilterSetting.splice(groupIndex, 1)
            _this.changed = true
        },
        removeChartRule (groupIndex, ruleIndex)
        {
            let _this = this
            _this.chartFilterSetting[groupIndex].rules.splice(ruleIndex, 1)
            _this.changed = true
        },
        addChartGroup ()
        {
            let _this = this
            _this.chartFilterSetting.push({ rules: [] })
            _this.changed = true

        },
        submit (text)
        {
            this.filterWordDialogVisible = false
            if (text != null)
            {
                this.dat[this.currentIndex].filterWord = text
                this.dat[this.currentIndex].filterWordDesc = this.getDescText(text)
                this.changed = true
            }
        },
        submitChartFilter (rule)
        {
            let _this = this
            if (rule != null)
            {
                if (_this.modifyRuleIndex === -1)
                {
                    _this.chartFilterSetting[_this.modifyGroupIndex].rules.push(rule)
                }
                else
                {
                    _this.chartFilterSetting[_this.modifyGroupIndex].rules[_this.modifyRuleIndex] = rule
                }
                _this.changed = true
            }
        },
        addRow ()
        {
            this.dat.push({
                minAge: 0,
                maxAge: 0,
                ageType: '介于',
                filterWord: '',
                filterWordDesc: ''
            })
            this.changed = true
        },
        removeRow (row)
        {
            let index = this.dat.indexOf(row)
            this.dat.splice(index, 1)
            this.changed = true
        },
        save ()
        {
            let res = []
            let _this = this
            for (const element of this.dat)
            {
                res.push({
                    maxAge: element.maxAge,
                    minAge: element.minAge,
                    ageType: _this.labels.indexOf(element.ageType),
                    text: element.filterWord
                })
            }
            let chartFilters = []
            for (const filter of _this.chartFilterSetting)
            {
                let rules = []
                for (const rule of filter.rules)
                {
                    let obj = {}
                    obj.columnSelectType = _this.columnSelectTypes.indexOf(rule.columnSelectType)
                    obj.columnIndex = rule.columnIndex - 1
                    obj.columnName = rule.columnName
                    obj.columnType = _this.columnTypes.indexOf(rule.columnType)
                    if (rule.numberPattern != null)
                    {
                        obj.numberPattern = {
                            min: rule.numberPattern.min,
                            max: rule.numberPattern.max,
                            type: _this.labels.indexOf(rule.numberPattern.type)
                        }
                    }
                    obj.stringPattern = rule.stringPattern
                    rules.push(obj)
                }
                chartFilters.push({rules: rules})
            }
            _this.$axios.put(this.$backend + '/saveFilterSetting',
                {
                    name: _this.$store.state.centerName,
                    filters: res,
                    reportPatternList: _this.reportPatternList,
                    chartFilters: chartFilters
                })
                .then(resp =>
                {
                    if (resp.status === 200)
                    {
                        this.$message.success('保存成功')
                        this.changed = false
                    }
                })
        },

        changeAge (row)
        {
            if (row.ageType == '介于' && row.minAge > row.maxAge)
            {
                let tmp = row.minAge
                row.minAge = row.maxAge
                row.maxAge = tmp
            }
            this.change()
        },

        change ()
        {
            this.changed = true
        },

        getDescText (text)
        {
            const andReg = new RegExp('and', 'gi')
            const orReg = new RegExp('or', 'gi')
            const notReg = new RegExp('not', 'gi')
            text = text.replaceAll('OR', `<span style="color: blue;"> OR </span>`)  // or should be done first because color contains `or`
            text = text.replaceAll('AND', `<span style="color: blue;"> AND </span>`)
            text = text.replaceAll('NOT', `<span style="color: blue;"> NOT </span>`)
            text = text.replaceAll('(', '<span style=\'color: orangered;\'>(</span>')
            text = text.replaceAll(')', '<span style=\'color: orangered;\'>)</span>')
            return text
        },
        getChartFilterText (cond)
        {
            let res
            let columnName
            if (cond.columnSelectType == '根据列的序号查找')
            {
                columnName = '<span style="color: navy">第' + cond.columnIndex + '列</span>'
            }
            else
            {
                columnName = '<span style="color: navy">"' + cond.columnName + '" 列</span>'
            }

            if (cond.columnType == '文本')
            {
                res = columnName + '满足筛选词：<span style="display: inline-block;">' + this.getDescText(cond.stringPattern) + '</span>'
            }
            else
            {
                switch (cond.numberPattern.type)
                {
                    case '介于':
                        res = cond.numberPattern.min + ' ≤ ' + columnName + ' ≤ ' + cond.numberPattern.max
                        break
                    case '小于':
                        res = columnName + ' ≤ ' + cond.numberPattern.max
                        break
                    case '大于':
                        res = columnName + ' ≥ ' + cond.numberPattern.min
                        break
                }
            }
            return res
            // return cond
            // return cond.columnSelectType
        }
    }
}
</script>

<style scoped>
.rule
{
    background: #ffffff00;
    transition: all 0.5s;
}

.rule:hover
{
    background: #e6ecf1;
    transition: all 0.5s;
}

.group:hover
{
    background: #f9fafb;
}
</style>
