import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '../views/HomePage.vue';
import ReportPage from '../views/ReportPage.vue';
import ReportDetailPage from '../views/ReportDetailPage.vue';
import AdminPage from '../views/AdminPage.vue';
import TestPage from '../views/TestPage.vue';
import NotificationsPage from '../views/NotificationsPage.vue'; // 알림 페이지 추가
import AllFireReportsView from '../views/AllFireReportsView.vue';
import AllClosedEventsView from '../views/AllClosedEventsView.vue';
import MapPage from '../views/MapPage.vue';
import FireReportDetail from '../views/admin/FireReportDetail.vue'; // 관리자용 화재 제보 상세 페이지 추가

const routes = [
  {
    path: "/login",
    name: "Login",
    component: () => import("../views/LoginPage.vue"),
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("../views/RegisterPage.vue"),
  },
  {
    path: '/',
    name: 'Home',
    component: HomePage
  },
  {
    path: '/report',
    name: 'Report',
    component: ReportPage
  },
  {
    path: '/report/:id',
    name: 'ReportDetail',
    component: ReportDetailPage,
    props: true
  },
  {
    path: '/report-success',
    name: 'report-success',
    component: () => import('../views/ReportSuccessPage.vue') // 지연 로딩 사용
  },
  {
    path: '/admin',
    name: 'Admin',
    component: AdminPage
  },
  {
    path: '/admin/all-fire-reports',
    name: 'AllFireReports',
    component: AllFireReportsView
  },
  {
    path: '/admin/all-closed-events',
    name: 'AllClosedEvents',
    component: AllClosedEventsView
  },
  {
    path: '/admin/full-risk-map',
    name: 'FullRiskMap',
    component: () => import('../views/admin/FullRiskMapPage.vue')
  },
  {
    path: '/admin/report/:id', // 관리자용 화재 제보 상세 페이지 경로 추가
    name: 'AdminFireReportDetail',
    component: FireReportDetail,
    props: true
  },
  {
    path: '/test',
    name: 'Test',
    component: TestPage
  },
  {
    path: '/notifications', // 알림 페이지 경로 추가
    name: 'Notifications',
    component: NotificationsPage
  },
  {
    path: '/map',
    name: 'Map',
    component: MapPage
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/ProfilePage.vue')
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
