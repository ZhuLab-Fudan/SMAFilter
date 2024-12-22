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
    direction="rtl"
    :model-value="visible"
    :close-on-click-modal="true"
    :close-on-press-escape="true"
    :before-close="handleClose"
    size="40%">
    <template #title>
        <span style="font-size: 20px; color: #555; font-weight: bolder;">可被筛出的样例</span>
    </template>
    <p v-if="examples.length === 0" style="font-size: 18px; color: #d75c37; font-weight: bold;">筛选词有误，无法筛出任何样例</p>
<!--    <p v-if="examples.length > 0" style="font-size: 18px; color: #777; font-weight: bold;">可被筛选出的样例</p>-->
    <div style="height: calc(100vh - 250px); overflow: scroll; margin: 10px 5% 0; width: 90%;">
        <!--            <div :style="{height: window.innerHeight}">-->
        <ol>
            <li v-for="example in examples" style="text-align: left; margin: 10px 0; font-size: 16px">{{ example }}</li>
        </ol>
    </div>
    <div style="text-align: left; font-size: 16px; display: block; width: 90%; margin: 20px 5% 0">
        <p>注意：</p>
        <p>1. 除了上述样例，其余形式均不会被筛出</p>
        <p>2. 关键词出现顺序不会造成影响</p>
    </div>
</el-drawer>
</template>

<script>
export default {
    name: 'ExampleDialog',
    data ()
    {
        return {
            visible: false,
            examples: []
        }
    },
    methods: {
        handleClose(done)
        {
            let _this = this
            _this.visible = false
            done()
        },
        close()
        {
            this.visible = false
        },
        show(examples)
        {
            let _this = this
            _this.examples = examples
            _this.visible = true
        }
    }
}
</script>

<style scoped>

</style>
