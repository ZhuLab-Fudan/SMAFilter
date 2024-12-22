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
    <h1>处理中</h1>
    <el-progress :percentage="percentage" :format="format" :status="status" stroke-width="20" color="#67c23a"/>
    <div ref="terminal"
         style="background:#eee;width: 100%; height: calc(100% - 200px); margin: 20px 0; text-align: left; padding: 5px 10px; overflow-y: scroll; white-space: pre-wrap; font-family: monospace">
<!--        {{contents}}-->
        <div v-for="(content, index) in contents" :key="content" style="margin: 0;" v-html="content"></div>
    </div>
    <el-button type="danger" @click="cancel" :disabled="!processing">取消处理</el-button>
</template>

<script>
export default {
    name: 'Processing',
    data ()
    {
        return {
            percentage: '0',
            status: '',
            contents: [],
            remainTime: '0 秒',
            intervalId: null,
            processing: true,
            format: (percentage) => `${percentage}% 剩余 ${this.remainTime}`
        }
    },
    mounted ()
    {
        // const socket = new WebSocket("ws://localhost:7890/progress")
        //
        // socket.onmessage = event => {
        //     let obj = JSON.parse(event.data);
        //     console.log(obj)
        // }

        let _this = this
        const terminalDiv = this.$refs.terminal
        this.intervalId = setInterval(function ()
        {
            _this.$axios.get(_this.$backend + '/getProgress')
                .then(resp =>
                {
                    if (resp.status === 200)
                    {
                        _this.contents = resp.data.info
                        _this.percentage = resp.data.percentage
                        _this.remainTime = resp.data.remainedTime
                        _this.$nextTick(function()
                        {
                            terminalDiv.scrollTop = terminalDiv.scrollHeight
                        })
                        if (resp.data.percentage == "100.00")
                        {
                            // console.log("here")
                            clearInterval(_this.intervalId)
                            _this.processing = false
                        }
                    }
                    else
                    {
                        clearInterval(_this.intervalId)
                    }
                })
                .catch(err =>
                {
                    // for (let i = 0;i < 200;i++)
                    // {
                    //
                    //     _this.contents.push("TestLine");
                    // }
                    // console.log(terminalDiv.scrollHeight)
                    // console.log(terminalDiv.clientHeight)
                    // console.log(terminalDiv.offsetHeight)
                    // _this.$nextTick(function()
                    // {
                    //     terminalDiv.scrollTo({top: terminalDiv.scrollHeight, behavior: 'smooth'})
                    // })
                    // terminalDiv.scrollTop = terminalDiv.scrollHeight + terminalDiv.clientHeight
                    if (err.response.status != 500 )
                    {
                        clearInterval(_this.intervalId)
                    }

                })
        }, 500)
    },
    methods:
        {
            cancel(){
                let _this = this
                clearInterval(_this.intervalId)
                _this.processing = false;
                this.$axios.post(this.$backend + "/stopProcess")
            }

        }
}
</script>

<style scoped>

</style>
