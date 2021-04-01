<template>
  <div class="home">
    <TabView>
      <TabPanel header="Flights">
        <Flights :user="user" />
      </TabPanel>
      <TabPanel header="Schedules with discount">
        <SchedulesWithDiscount />
      </TabPanel>
    </TabView>
  </div>
</template>

<script>
import TabView from 'primevue/tabview'
import TabPanel from 'primevue/tabpanel'
import Flights from '../components/Flights.vue'
import SchedulesWithDiscount from '../components/SchedulesWithDiscount.vue'

export default {
  name: 'Home',
  components: {
    TabView,
    TabPanel,
    Flights,
    SchedulesWithDiscount,
  },
  props: {
    user: Object,
  },
}
</script>
