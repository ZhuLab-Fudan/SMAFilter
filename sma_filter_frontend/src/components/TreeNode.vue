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
    <div class="tree-node" style="display: block;margin: 10px 0;">
        <div v-if="isLeaf" style="display: flex; margin: 0 10px">
            <el-input v-model="node.value" placeholder="Enter value" style="width: 100px; display: inline-block"></el-input>
<!--            <el-button style="display: inline-block; margin: 0 10px" type="danger" round>删除</el-button>-->
            <el-button style="display: inline-block; margin: 0 10px; padding: 0 8px;font-size: 16px" type="primary" plain >
                <el-icon style="font-size: 16px">
                    <Edit/>
                </el-icon>
                <!--                        <p style="display: inline">-->
                修改
                <!--                        </p>-->
            </el-button><br>
        </div>
        <div v-else style="">
<!--            <el-button @click="addChildNode">Add Child</el-button>-->
<!--            <el-button @click="removeNode">Remove</el-button>-->
            <!-- <div style="display: inline-block; width: 30%;background:#ccc;vertical-align: middle">  -->
            <el-card style="margin: 0 10px;overflow-x: visible; display: flex">
                <div style="display: inline-block; min-width: 100px;vertical-align: middle; height: 80px; ">
                    <div style="display: flex;margin: 0 10px; height: 50%;">

                        <p style="vertical-align: middle;text-align: center; width: 100%;">{{ node.operator }}</p>
                    </div>
                    <el-button style="display: inline; vertical-align: middle; height: 50%;margin: 0; padding: 0 8px; font-size: 16px" type="primary" plain>
                        <el-icon style="font-size: 16px">
                            <Edit/>
                        </el-icon>
<!--                        <p style="display: inline">-->
                            修改
<!--                        </p>-->
                    </el-button><br>
                </div>
    <!--            <div style="display: inline-block;width: 60%;background:#9999fe;vertical-align: middle">-->
                <div style="display: inline-block;min-width: 200px;vertical-align: middle; border-left: solid #777 3px; margin: 10px 0">

                        <TreeNode v-for="child in node.children" :key="child.id" :node="child"/>
<!--                        <el-button style="display: block; margin: 0 10px; line-height: 10px" type="success" round>-->
<!--                            <el-icon style="font-size: 14px">-->
<!--                                <Plus/>-->
<!--                            </el-icon>-->
<!--                        </el-button>-->

                </div>
            </el-card>
        </div>
    </div>
</template>

<script>
export default {
    name: 'TreeNode',
    props: {
        node: {
            type: Object,
            required: true
        }
    },
    computed: {
        isLeaf ()
        {
            return !this.node.children || this.node.children.length === 0
        }
    },
    methods: {
        addChildNode ()
        {
            this.$set(this.node, 'children', [
                ...this.node.children,
                {
                    operator: 'AND',
                    children: []
                } // You can customize the default structure
            ])
        },
        removeNode ()
        {
            const parentNode = this.$parent.node
            const index = parentNode.children.indexOf(this.node)
            if (index !== -1)
            {
                parentNode.children.splice(index, 1)
            }
        }
    }
}
</script>

<style scoped>

</style>
