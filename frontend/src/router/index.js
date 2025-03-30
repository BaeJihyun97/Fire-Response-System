import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '../views/HomePage.vue';
import ReportPage from '../views/ReportPage.vue';
import ReportDetailPage from '../views/ReportDetailPage.vue';
import AdminPage from '../views/AdminPage.vue';
import TestPage from '../views/TestPage.vue';
import NotificationsPage from '../views/NotificationsPage.vue'; // 알림 페이지 추가
import AllFireReportsView from '../views/AllFireReportsView.vue';
import AllClosedEventsView from '../views/AllClosedEventsView.vue';
const routes = [
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
    path: '/test',
    name: 'Test',
    component: TestPage
  },
  {
    path: '/notifications', // 알림 페이지 경로 추가
    name: 'Notifications',
    component: NotificationsPage
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;