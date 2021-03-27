<template>
  <div class="home">
    <TabView>
      <TabPanel header="Flights">
        <Flights />
      </TabPanel>
      <TabPanel header="Schedules with discount">
        Content II
      </TabPanel>
    </TabView>
  </div>
</template>

<script>
import TabView from 'primevue/tabview'
import TabPanel from 'primevue/tabpanel'
import Flights from '../components/Flights.vue'

export default {
  name: 'Home',
  components: {
    TabView,
    TabPanel,
    Flights,
  }
}
</script>
