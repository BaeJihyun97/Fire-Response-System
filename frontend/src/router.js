
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import ReportmanagementReportManager from "./components/listers/ReportmanagementReportCards"
import ReportmanagementReportDetail from "./components/listers/ReportmanagementReportDetail"

import VideoproccessingVideoAnalysisManager from "./components/listers/VideoproccessingVideoAnalysisCards"
import VideoproccessingVideoAnalysisDetail from "./components/listers/VideoproccessingVideoAnalysisDetail"
import VideoproccessingVideoManager from "./components/listers/VideoproccessingVideoCards"
import VideoproccessingVideoDetail from "./components/listers/VideoproccessingVideoDetail"

import EventmanagementEventManager from "./components/listers/EventmanagementEventCards"
import EventmanagementEventDetail from "./components/listers/EventmanagementEventDetail"

import AianalysisAiAnalysisManager from "./components/listers/AianalysisAiAnalysisCards"
import AianalysisAiAnalysisDetail from "./components/listers/AianalysisAiAnalysisDetail"

import FeedservicePostManager from "./components/listers/FeedservicePostCards"
import FeedservicePostDetail from "./components/listers/FeedservicePostDetail"

import NotificationNotificationManager from "./components/listers/NotificationNotificationCards"
import NotificationNotificationDetail from "./components/listers/NotificationNotificationDetail"


export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/reportmanagements/reports',
                name: 'ReportmanagementReportManager',
                component: ReportmanagementReportManager
            },
            {
                path: '/reportmanagements/reports/:id',
                name: 'ReportmanagementReportDetail',
                component: ReportmanagementReportDetail
            },

            {
                path: '/videoproccessings/videoAnalyses',
                name: 'VideoproccessingVideoAnalysisManager',
                component: VideoproccessingVideoAnalysisManager
            },
            {
                path: '/videoproccessings/videoAnalyses/:id',
                name: 'VideoproccessingVideoAnalysisDetail',
                component: VideoproccessingVideoAnalysisDetail
            },
            {
                path: '/videoproccessings/videos',
                name: 'VideoproccessingVideoManager',
                component: VideoproccessingVideoManager
            },
            {
                path: '/videoproccessings/videos/:id',
                name: 'VideoproccessingVideoDetail',
                component: VideoproccessingVideoDetail
            },

            {
                path: '/eventmanagements/events',
                name: 'EventmanagementEventManager',
                component: EventmanagementEventManager
            },
            {
                path: '/eventmanagements/events/:id',
                name: 'EventmanagementEventDetail',
                component: EventmanagementEventDetail
            },

            {
                path: '/aianalyses/aiAnalyses',
                name: 'AianalysisAiAnalysisManager',
                component: AianalysisAiAnalysisManager
            },
            {
                path: '/aianalyses/aiAnalyses/:id',
                name: 'AianalysisAiAnalysisDetail',
                component: AianalysisAiAnalysisDetail
            },

            {
                path: '/feedservices/posts',
                name: 'FeedservicePostManager',
                component: FeedservicePostManager
            },
            {
                path: '/feedservices/posts/:id',
                name: 'FeedservicePostDetail',
                component: FeedservicePostDetail
            },

            {
                path: '/notifications/notifications',
                name: 'NotificationNotificationManager',
                component: NotificationNotificationManager
            },
            {
                path: '/notifications/notifications/:id',
                name: 'NotificationNotificationDetail',
                component: NotificationNotificationDetail
            },



    ]
})
