<template>
  <Bar
    :chart-data="chartData"
    :chart-options="chartOptions"
  />
</template>

<script lang="ts">
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale,
} from 'chart.js';
import { Bar } from 'vue-chartjs/legacy';
import { Component, Prop, Vue } from 'vue-property-decorator';
import QuestionStats from '@/models/statistics/QuestionStats';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
);

type AbstractStats = keyof QuestionStats;

export interface StatsConfig {
  color: string;
  label: string;
  property: AbstractStats;
}

interface Dataset {
  label: string;
  backgroundColor: string;
  data: number[];
}

@Component({
  components: { Bar },
})
export default class StatsBarChart extends Vue {
  @Prop() readonly statsData!: QuestionStats[];
  @Prop() readonly statsConfig!: StatsConfig[];

  getExecutionYears(): string[] {
    return this.statsData.map((stat) => stat.courseExecutionYear.toString());
  }

  getDatasets() {
    const datasets: Dataset[] = [];
    this.statsConfig.forEach((statConfig) => {
      try {
        const statData = this.statsData.map(
          (stat) => stat[statConfig.property]
        );

        datasets.push({
          label: statConfig.label,
          backgroundColor: statConfig.color,
          data: statData,
        });
      } catch (e) {}
    });

    return datasets;
  }

  chartData = {
    labels: this.getExecutionYears().reverse(),
    datasets: this.getDatasets().reverse(),
  };

  chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
  };

}
</script>
