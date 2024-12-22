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
    <!--  <img src="./assets/logo.png">-->
    <!--  <div>-->
    <!--    <p>-->
    <!--      If Element Plus is successfully added to this project, you'll see an-->
    <!--      <code v-text="'<el-button>'"></code>-->
    <!--      below-->
    <!--    </p>-->
    <!--    <el-button type="primary">el-button</el-button>-->
    <!--  </div>-->
    <!--  <HelloWorld msg="Welcome to Your Vue.js App"/>-->
    <!--  <SMAFilter/>-->
    <router-view></router-view>
</template>

<script>
// import HelloWorld from './components/HelloWorld.vue'

export default {
    name: 'App',
    components: {}
}
</script>

<style>
html, body
{
    margin: 0;
    height: 100%;
}

#app
{
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    /*margin-top: 60px;*/
    height: 100%;
}
</style>
