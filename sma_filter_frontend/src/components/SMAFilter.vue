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
    <main style="height: 100%;">
        <el-container>
            <el-aside width="120px" style="z-index: 10;">
<!--                <el-menu router="true" :default-active="$route.name" class="el-menu-vertical-demo" :collapse="isCollapse" width="40px"-->
                <el-menu router="true" default-active="3" class="el-menu-vertical-demo" :collapse="isCollapse" width="40px" @select="changePage"
                         style="overflow: visible" >
                    <el-menu-item index="/CenterSelect" class="sideButton">
                        <div class="menuitem" style="width: 100%">
                        <p class="sideButtonText" style="display: inline-block">
                            <el-icon class="sideButtonIcon" style="display: inline;">
                                <HomeFilled/>
                            </el-icon>
                            欢迎
                        </p>
<!--                            <template #title>欢迎</template>-->
                        </div>
                    </el-menu-item>
                    <el-menu-item index="/Filter" class="sideButton" :disabled="this.$store.state.centerName == ''">
                        <div class="menuitem">
                            <p class="sideButtonText">
                                <el-icon class="sideButtonIcon">
                                    <Filter/>
                                </el-icon>
                                开始筛选</p>
<!--                            <template #title>开始筛选</template>-->
                        </div>
                    </el-menu-item>
                    <el-menu-item index="/Result" class="sideButton" :disabled="this.$store.state.centerName == ''">
                        <div class="menuitem">

<!--                            <template #title>筛选结果</template>-->
                            <p class="sideButtonText">
                                <el-icon class="sideButtonIcon">
                                    <Files/>
                                </el-icon>
                                筛选结果</p>
                        </div>
                    </el-menu-item>
                    <el-menu-item index="/Setting" class="sideButton" :disabled="this.$store.state.centerName == ''">
                        <div class="menuitem">

                            <p class="sideButtonText">
                                <el-icon class="sideButtonIcon">
                                    <setting/>
                                </el-icon>
                                筛选设置</p>
<!--                            <template #title>筛选设置</template>-->
                        </div>
                    </el-menu-item>
                </el-menu>
            </el-aside>

            <el-main style="padding-right: 5%;padding-left: calc(5% + 30px);padding-top: 50px; overflow-: scroll;white-space: nowrap">
                <router-view></router-view>
            </el-main>
        </el-container>
    </main>
</template>

<script>
export default {
    name: 'SMAFilter',
    data()
    {
        return {
            // defaultActive: '/CenterSelect'
            currentRoute: this.$route.path
        }
    },
    methods: {
        changePage()
        {
            console.log("?")
        }
    }
}
</script>

<style scoped>
.el-container
{
    height: 100%;
}

.el-menu-item
{
    padding: 0;
    padding-left: 0!important;
}

.sideButtonIcon
{
    display: inline;
    padding: 0 5px;
}
.sideButtonText
{
    display: inline-block;
    margin: 0;
    font-size: 16px;
    width: 100%;
}

.menuitem
{
    height: 100%;
    width: 100%;
}

/*.el-menu-item.is-active {*/
/*    position: relative;*/
/*}*/

.el-menu-item.is-active .menuitem p ::after
{
    display: none;
}

.el-menu-item.is-active .menuitem  ::after
{
    top: 50%;
    left: 119px;
    /*margin: 0;*/
    margin-left: 0!important;
    /*right: 10px;*/
    border-style: solid;
    display: block;
    border-width: 30px 0 30px 30px;
    border-color: transparent transparent transparent #ddd;
    /*borderleft: 5px solid #ddd;*/
    /*borderleft: 5px solid #f00;*/
    transform: translateY(-50%);
    margin-left: 8px;
    /*height: 30px;*/
    /*width: 30px;*/
    /*background: #f00;*/
    /*color: #f00;*/
    position: absolute;
    /*position: fixed;*/
    content: '';
    /*z-index: 20;*/
    overflow: visible;
    /*right: 10px;*/
}
/*.el-menu-item.is-active .menuitem :hover*/
/*{*/
/*    background: #000;*/
/*}*/


/*.el-menu-item.is-active .menuitem :hover ::after*/
/*{*/
/*    border-color: transparent transparent transparent #000!important;*/
/*}*/



.el-aside
{
    background: #ddd;
    overflow: visible;
}

.el-menu
{
    background: #ddd;
    overflow: visible;
}

/*i*/
/*{*/
/*    width: 18px;*/
/*    height: 18px;*/
/*}*/

.el-icon
{
    font-size: 20px
}


</style>
