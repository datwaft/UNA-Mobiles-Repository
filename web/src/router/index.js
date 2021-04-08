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
