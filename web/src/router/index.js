import { createRouter, createWebHistory } from "vue-router";
import Home from "../views/Home.vue";

const routes = [
  {
    path: "/",
    name: "Home",
    component: Home,
  },
  {
    path: "/user/user-information",
    name: "User Information",
    component: () => import("@/views/user/UserInformation.vue"),
  },
  {
    path: "/user/purchase-history",
    name: "Purchase History",
    component: () => import("@/views/user/PurchaseHistory.vue"),
  },
  {
    path: "/admin/plane-type",
    name: "Plane Type",
    component: () => import("@/views/admin/PlaneType.vue"),
  },
  {
    path: "/admin/route",
    name: "Route",
    component: () => import("@/views/admin/Route.vue"),
  },
  {
    path: "/admin/plane",
    name: "Plane",
    component: () => import("@/views/admin/Plane.vue"),
  },
  {
    path: "/admin/schedule",
    name: "Schedule",
    component: () => import("@/views/admin/Schedule.vue"),
  },
  {
    path: "/admin/flight",
    name: "Flight",
    component: () => import("@/views/admin/Flight.vue"),
  },
  {
    path: "/admin/report",
    name: "Report",
    component: () => import("@/views/admin/Report.vue"),
  },
  {
    path: "/about/history",
    name: "History",
    component: () => import("@/views/about/History.vue"),
  },
  {
    path: "/about/contact",
    name: "Contact Information",
    component: () => import("@/views/about/Contact.vue"),
  },
  {
    path: "/about/reference",
    name: "Institutional Reference",
    component: () => import("@/views/about/Reference.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
