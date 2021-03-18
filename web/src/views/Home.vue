<template>
  <div class="home">
    <TabView>
      <TabPanel header="Header I">
        Content I
      </TabPanel>
      <TabPanel header="Header II">
        Content II
      </TabPanel>
      <TabPanel header="Header III">
        Content III
      </TabPanel>
    </TabView>
  </div>
</template>

<script>
import TabView from 'primevue/tabview'
import TabPanel from 'primevue/tabpanel'

export default {
  name: 'Home',
  components: {
    TabView,
    TabPanel,
  }
}
</script>
