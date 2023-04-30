<template>
  <div class="container">
    <h1>Teacher Dashboard</h1>

    <div v-if="show === 'Global'" class="stats-container">
      <teacher-stats-view :dashboardId="dashboardId"></teacher-stats-view>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import TeacherStatsView from '@/views/teacher/dashboard/TeacherStatsView.vue';

@Component({
  components: {
    TeacherStatsView
  },
})
export default class TeacherDashboardView extends Vue {
  dashboardId: number | null = null;
  show: string | null = null;

  async created() {
    await this.$store.dispatch('loading');
    try {
      let teacherDashboard = await RemoteServices.getTeacherDashboard();

      this.dashboardId = teacherDashboard.id;
      this.show = 'Global';
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>
