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
import { createRouter, createWebHashHistory } from 'vue-router'
import SMAFilter from '@/components/SMAFilter'
import CenterSelect from '@/components/page/home/CenterSelect'
import FileSelect from '@/components/page/filter/FileSelect'
import FilterResult from '@/components/page/result/FilterResult'
import FilterSetting from '@/components/page/setting/FilterSetting'
import Processing from '@/components/page/filter/Processing'
import CenterSetting from '@/components/page/home/CenterSetting'

const routes = [
    {
        path: '/',
        name: 'home',
        component: SMAFilter,
        redirect: '/CenterSelect',
        children: [
            {
                path: '/CenterSelect',
                name: 'centerSelect',
                component: CenterSelect
            },
            {
                path: '/Filter',
                name: 'filter',
                component: FileSelect
            },
            {
                path: '/Result',
                name: 'result',
                component: FilterResult
            },
            {
                path: '/Setting',
                name: 'setting',
                component: FilterSetting
            },
            {
                path: '/Processing',
                name: 'processing',
                component: Processing
            },
            {
                path: '/CenterSetting',
                name: 'centerSetting',
                component: CenterSetting
            }
        ]
    }
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})

export default router
