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
        direction="btt"
        :model-value="dialogVisible"
        :close-on-press-escape="false"
        :close-on-click-modal="false"
        :before-close="handleClose"
        size="40%"
        >
        <template #title>
            <span style="font-size: 20px; color: #555; font-weight: bolder">设置筛选词</span>
        </template>
        <div style="width: 80%; display: inline-block; height: calc(40vh - 80px); overflow-y: scroll">

            <el-input v-model="text" type="textarea" :autosize="{minRows: 1, maxRows:3}" style="width: 90%; font-size: 16px" @change="checkExpression"/>

            <div v-if="examples.length === 0" style="font-size: 18px; color: #d75c37; font-weight: bold;">筛选词有误，无法筛出任何样例 {{ errorMessage }}</div>
            <div v-if="examples.length > 0" style="font-size: 18px; color: #379928; font-weight: bold;">
                共可筛出 {{ examples.length }} 种样例
                <el-button type="text" @click="showExample" style="font-size: 18px">查看详情</el-button>
            </div>

            <div style="margin-top: 30px;margin-bottom: 10px; text-align: center; display: block; width: 100%">
                <el-button @click="close" type="primary" plain style="margin: 0px 20px">取消</el-button>
                <el-button @click="submit" type="primary" style="margin: 0px 20px" :disabled="!checked">确认</el-button>
            </div>
        </div>
    </el-drawer>
</template>

<script>
import ExampleDialog from '@/components/dialog/ExampleDialog'
export default {
    name: 'FilterWordDialog',
    components: { ExampleDialog },
    emits: ['ret'],
    data ()
    {
        return {
            dialogVisible: false,
            text: '',
            checked: true,
            examples: [],
            errorMessage: ''
        }
    },
    methods: {
        wash()
        {
            const andReg = new RegExp('and', 'gi')
            const orReg = new RegExp('or', 'gi')
            const notReg = new RegExp('not', 'gi')
            this.text = this.text.replace(orReg, match => `OR`)
            this.text = this.text.replace(andReg, match => `AND`)
            this.text = this.text.replace(notReg, match => `NOT`)
            this.text = this.text.replace("（", "(").replace("）", ")")
        },
        show(text)
        {
            this.dialogVisible = true
            this.text = text
            this.checkExpression()
        },
        submit()
        {
            this.wash()
            this.dialogVisible = false;
            this.$emit('ret', this.text)
        },
        close()
        {
            this.dialogVisible = false;
            this.$emit('ret', null)
            this.examples = []
            this.text = ''
            // this.$emit('ret', "A and (B or C)")
        },
        checkExpression()
        {
            // this.$message.info("检查表达式")
            this.wash()
            this.checked = false;
            let _this = this
            _this.examples = []
            _this.$axios.put(_this.$backend + "/checkExpressionAvailable", {
                name: _this.text
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
        }
    }
}
</script>

<style scoped>

</style>
