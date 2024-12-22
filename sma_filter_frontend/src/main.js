/*
 * Copyright [2024] [Institute of Science and Technology for Brain-Inspired Intelligence, Fudan University]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// import Vue from 'vue'
// import ElementUI,{ Table } from 'element-ui'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
// import { ElIcon } from 'element-plus'
// import 'element-plus/lib/theme-chalk/icon.css'
import * as ElementPlusIconsVue from '@element-plus/icons'

import installElementPlus from './plugins/element.js'

const axios = require('axios')
axios.default.baseURL = '/api'
const app = createApp(App)
installElementPlus(app)
// app.component(ElIcon.name, ElIcon)
app.use(store).use(router)
app.config.globalProperties.$axios = axios
// app.config.globalProperties.$backend = 'http://localhost:7890'
app.config.globalProperties.$backend = 'http://localhost:7890'
// app.config.globalProperties.$filterJson = []
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
app.mount('#app')

//
// const debounce = (fn, delay) => {
//     let timer = null;
//     return function () {
//         let context = this;
//         let args = arguments;
//         clearTimeout(timer);
//         timer = setTimeout(function () {
//             fn.apply(context, args);
//         }, delay);
//     }
// }
//
// const _ResizeObserver = window.ResizeObserver;
// window.ResizeObserver = class ResizeObserver extends _ResizeObserver{
//     constructor(callback) {
//         callback = debounce(callback, 1000);
//         super(callback);
//     }
// }
