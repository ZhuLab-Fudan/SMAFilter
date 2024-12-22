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
    <example-dialog ref="exampleDialog"/>
    <el-drawer
        direction="rtl"
        :model-value="visible"
        :close-on-press-escape="false"
        :close-on-click-modal="false"
        :before-close="handleClose"
        size="80%"
    >
        <template #title>
            <span style="font-size: 20px; color: #555; font-weight: bolder;">设置筛选条件</span>
        </template>
        <div style="width: 100%; height: calc(100vh - 100px); padding: 0 10%; overflow-y: scroll">
            <el-card style="margin: 0 0 20px; text-align: left">
                <template #header>
                    <span style="font-size: 16px; color: #777; font-weight: bold;">选择表中的一列</span>
                </template>
                <div style="margin: 0 10px 10px">
                    <span>查找方式：</span>
                    <el-select v-model="rule.columnSelectType" @change="checkColumn" style="margin: 0 20px 5px">
                        <el-option label="根据列的序号查找" value="根据列的序号查找"/>
                        <el-option label="根据列的名称查找" value="根据列的名称查找"/>
                    </el-select>
                </div>
                <div v-if="rule.columnSelectType == '根据列的序号查找'" style="margin: 10px 10px 0">
                    <span>列的序号：第</span>
                    <el-input-number v-model="rule.columnIndex" :min="1" @change="checkColumn" style="margin: 5px 20px 0;width: 150px"/>
                    <span>列</span>
                </div>
                <div v-if="rule.columnSelectType == '根据列的名称查找'" style="margin: 10px 10px 0">
                    <span>列的名称：</span>
                    <el-input v-model="rule.columnName" @change="checkColumn" style="margin: 5px 20px 0; width: calc(100% - 150px); font-size: 16px"/>
                    <div v-if="!columnSelect" style="font-size: 18px; color: #d75c37; font-weight: bold;margin-top: 20px">名称不能为空</div>
                </div>
            </el-card>
            <el-card style="margin: 20px 0 0; text-align: left">
                <template #header>
                    <span style="font-size: 16px; font-weight: bold; color: #777;">满足的条件</span>
                </template>
                <div style="margin: 0 10px 10px">
                    <span>列的内容：</span>
                    <el-select v-model="rule.columnType" @change="checkExpression" style="margin: 0 20px 5px">
                        <el-option label="数字" value="数字"/>
                        <el-option label="文本" value="文本"/>
                    </el-select>
                </div>
                <div v-if="rule.columnType == '数字'" style="margin: 10px 10px 0">
                    <p style="display: inline-block; width: 100px">满足条件：</p>
                    <el-select v-model="rule.numberPattern.type" style="width: 130px;" @change="checkExpression">
                        <el-option label="大于" value="大于"/>
                        <el-option label="介于" value="介于"/>
                        <el-option label="小于" value="小于"/>
                    </el-select>
                    <el-input-number v-if="rule.numberPattern.type == '介于' || rule.numberPattern.type == '大于'" v-model="rule.numberPattern.min"
                                     style="width: 150px;margin: 10px" @change="checkExpression"/>
                    <el-input-number v-if="rule.numberPattern.type == '小于'" v-model="rule.numberPattern.max"
                                     style="width: 150px;margin: 10px" @change="checkExpression"/>
                    <div v-if="rule.numberPattern.type =='介于'" style="display: inline-block">
<!--                        <p style="display: inline-block; width: 100px; background:#000;"></p>-->
                        <p style="display: inline-block;">~</p>
                        <el-input-number v-model="rule.numberPattern.max" style="width: 150px;margin: 10px"
                                         @change="checkExpression"/>
                    </div>
                </div>
                <div v-if="rule.columnType == '文本'" style="margin: 10px 10px 0">
                    <span>满足条件：</span>
                    <el-input v-model="rule.stringPattern" type="textarea" :autosize="{minRows: 1, maxRows:3}" @change="checkExpression" style="margin: 5px 20px 0; width: calc(100% - 150px); font-size: 16px"/>
                    <div v-if="examples.length === 0" style="font-size: 18px; color: #d75c37; font-weight: bold;margin-top: 20px">筛选词有误，无法筛出任何样例 {{ errorMessage }}</div>
                    <div v-if="examples.length > 0" style="font-size: 18px; color: #379928; font-weight: bold;margin-top: 20px">
                        共可筛出 {{ examples.length }} 种样例
                        <el-button type="text" @click="showExample" style="font-size: 18px">查看详情</el-button>
                    </div>
                </div>
            </el-card>
            <div style="margin-top: 30px;margin-bottom: 50px; text-align: center; display: block; width: 100%; float: bottom;">
                <el-button @click="close" type="primary" plain style="margin: 0px 20px">取消</el-button>
                <el-button @click="submit" type="primary" style="margin: 0px 20px" :disabled="!(checked && columnSelect)">确认</el-button>
            </div>
        </div>

    </el-drawer>
</template>

<script>
import ExampleDialog from '@/components/dialog/ExampleDialog'
export default {
    name: 'ChartFilterRuleDialog',
    components: { ExampleDialog },
    emits: ['ret'],
    data ()
    {
        return {
            visible: false,
            rule:                     {
                columnSelectType: '根据列的序号查找',
                columnIndex: 8,
                columnType: '文本',
                stringPattern: '(广泛神经源 OR 神经源性损害) AND (上肢 OR 下肢 OR 脊髓前角 OR 胸锁乳突肌 OR 上下肢)'
            },
            checked: true,
            columnSelect: true,
            examples: [],
            errorMessage: ''
        }
    },
    mounted ()
    {
        this.checkExpression()
    },
    methods: {
        show (rule)
        {
            let _this = this
            _this.rule = rule
            _this.visible = true
            _this.checkExpression()
            console.log(rule)
        },
        close()
        {
            let _this = this
            _this.visible = false
            _this.rule = {}
            _this.$emit('ret', null)
            _this.examples = []
        },
        submit()
        {
            let _this = this
            _this.visible = false
            _this.$emit('ret', _this.rule)
            _this.rule = {}
            _this.examples = []
        },
        handleClose(done)
        {
            this.close()
            done()
        },
        showExample()
        {
            let _this = this
            _this.$refs.exampleDialog.show(_this.examples)
        },
        checkColumn()
        {
            let _this = this
            _this.columnSelect = true;
            if (_this.rule.columnSelectType == '根据列的名称查找')
            {
                if ( _this.rule.columnName == '' ||  _this.rule.columnName == null)
                {
                    _this.columnSelect = false;
                }
            }
        },
        checkExpression ()
        {
            let _this = this
            if (_this.rule.columnType == '文本')
            {
                if (_this.rule.stringPattern == null)
                {
                    _this.rule.stringPattern = ''
                }
                let text = _this.washText(_this.rule.stringPattern)
                _this.rule.stringPattern = text
                _this.checked = false;
                _this.examples = []
                _this.$axios.put(_this.$backend + "/checkExpressionAvailable", {
                    name: text
                }).then(resp => {
                    if (resp.status === 200)
                    {
                        _this.examples = resp.data
                        _this.checked = true
                        _this.errorMessage = ''
                    }
                }).catch(err => {
                    if (err.response.status === 400)
                    {
                        _this.errorMessage = '。' + err.response.data[0]
                        // _this.$message.error(err.response.data[0])
                    }
                })
            }
            else  // should be number
            {
                _this.checked = true
                if (_this.rule.numberPattern == null)
                {
                    _this.rule.numberPattern = {min: 0, max: 100, type: '介于'}
                }
                if (_this.rule.numberPattern.min > _this.rule.numberPattern.max)
                {
                    let originalMin = _this.rule.numberPattern.min
                    _this.rule.numberPattern.min = _this.rule.numberPattern.max
                    _this.rule.numberPattern.max = originalMin
                }
            }
        },
        washText(text)
        {
            const andReg = new RegExp('and', 'gi')
            const orReg = new RegExp('or', 'gi')
            const notReg = new RegExp('not', 'gi')
            text = text.replace(orReg, match => `OR`)
            text = text.replace(andReg, match => `AND`)
            text = text.replace(notReg, match => `NOT`)
            text = text.replace("（", "(").replace("）", ")")
            return text
        }
    }
}
</script>

<style scoped>

</style>
