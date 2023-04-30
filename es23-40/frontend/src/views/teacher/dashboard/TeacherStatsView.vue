<template>
  <div class="container">
    <h2>Statistics for this course execution</h2>
    <div v-if="teacherDashboard != null" class="stats-container">
      <div
        v-if="teacherDashboard.studentStats[0] != null"
        class="stats-container"
      >
        <div class="items">
          <div ref="totalStudents" data-cy="totalStudents" class="icon-wrapper">
            <animated-number
              :number="teacherDashboard.studentStats[0].numStudents"
            />
          </div>
          <div class="project-name">
            <p>Number of Students</p>
          </div>
        </div>

        <div class="items">
          <div ref="atLeast75" data-cy="atLeast75" class="icon-wrapper">
            <animated-number
              :number="
                teacherDashboard.studentStats[0].numMore75CorrectQuestions
              "
            />
          </div>
          <div class="project-name">
            <p>Number of Students who solved >= 75% Questions</p>
          </div>
        </div>

        <div class="items">
          <div ref="moreThan3" data-cy="moreThan3" class="icon-wrapper">
            <animated-number
              :number="teacherDashboard.studentStats[0].numAtLeast3Quizzes"
            />
          </div>
          <div class="project-name">
            <p>Number of Students who solved >= 3 Quizzes</p>
          </div>
        </div>
      </div>
      <div v-if="teacherDashboard.quizStats[0] != null" class="stats-container">
        <div class="items">
          <div ref="totalQuizzes" data-cy="totalQuizzes" class="icon-wrapper">
            <animated-number
              :number="teacherDashboard.quizStats[0].numQuizzes"
            />
          </div>
          <div class="project-name">
            <p>Number of Quizzes</p>
          </div>
        </div>

        <div class="items">
          <div ref="uniqueQuizzes" data-cy="uniqueQuizzes" class="icon-wrapper">
            <animated-number
              :number="teacherDashboard.quizStats[0].numUniqueAnsweredQuizzes"
            />
          </div>
          <div class="project-name">
            <p>Number of Quizzes Solved (Unique)</p>
          </div>
        </div>

        <div class="items">
          <div ref="averageQuizzes" data-cy="averageQuizzes" class="icon-wrapper">
            <animated-number
              :number="teacherDashboard.quizStats[0].averageQuizzesSolved"
            />
          </div>
          <div class="project-name">
            <p>Number of Quizzes Solved (Unique, Average Per Student)</p>
          </div>
        </div>
      </div>
      <div v-if="teacherDashboard.questionStats[0] != null" class="stats-container">
        <div class="items">
          <div ref="totalQuestions" data-cy="totalQuestions" class="icon-wrapper">
            <animated-number
              :number="teacherDashboard.questionStats[0].numAvailable"
            />
          </div>
          <div class="project-name">
            <p>Number of Questions</p>
          </div>
        </div>

        <div class="items">
          <div ref="uniqueQuestions" data-cy="uniqueQuestions" class="icon-wrapper">
            <animated-number
              :number="teacherDashboard.questionStats[0].answeredQuestionsUnique"
            />
          </div>
          <div class="project-name">
            <p>Number of Questions Solved (Unique)</p>
          </div>
        </div>

        <div class="items">
          <div ref="averageQuestions" data-cy="averageQuestions" class="icon-wrapper">
            <animated-number
              :number="
                teacherDashboard.questionStats[0].averageQuestionsAnswered
              "
            />
          </div>
          <div class="project-name">
            <p>
              Number of Questions Correctly Solved (Unique, Average Per Student)
            </p>
          </div>
        </div>
      </div>
    </div>

    <div v-if="teacherDashboard != null" class="stats-container">
      <div class="bar-chart">
        <stats-bar-chart
          :stats-data="teacherDashboard.studentStats"
          :stats-config="[
            {
              label: 'Total number of students',
              color: '#c0392c',
              property: 'numStudents',
            },
            {
              label: 'Students who solved >= 75% Questions',
              color: '#2980b9',
              property: 'numMore75CorrectQuestions',
            },
            {
              label: 'Students who solved >= 3 Quizzes',
              color: '#1abc9c',
              property: 'numAtLeast3Quizzes',
            },
          ]"
        />
      </div>
    </div>

    <div v-if="teacherDashboard != null" class="stats-container">
      <div class="bar-chart">
        <stats-bar-chart
          :stats-data="teacherDashboard.quizStats"
          :stats-config="[
            {
              label: 'Quizzes: Total Available',
              color: '#c0392c',
              property: 'numQuizzes',
            },
            {
              label: 'Quizzes: Total Solved (Unique)',
              color: '#2980b9',
              property: 'numUniqueAnsweredQuizzes',
            },
            {
              label: 'Quizzes: Solved (Unique, Average per Student)',
              color: '#1abc9c',
              property: 'averageQuizzesSolved',
            },
          ]"
        />
      </div>
    </div>

    <div v-if="teacherDashboard != null" class="stats-container">
      <div class="bar-chart">
        <stats-bar-chart
          :stats-data="teacherDashboard.questionStats"
          :stats-config="[
            {
              label: 'Questions: Total Available',
              color: '#c0392c',
              property: 'numAvailable',
            },
            {
              label: 'Questions: Total Solved (Unique)',
              color: '#2980b9',
              property: 'answeredQuestionsUnique',
            },
            {
              label:
                'Questions: Correctly Solved (Unique, Average per Student)',
              color: '#1abc9c',
              property: 'averageQuestionsAnswered',
            },
          ]"
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
import TeacherDashboard from '@/models/dashboard/TeacherDashboard';
import StatsBarChart from '@/components/StatsBarChart.vue';

@Component({
  components: { StatsBarChart, AnimatedNumber },
})
export default class TeacherStatsView extends Vue {
  @Prop() readonly dashboardId!: number;
  teacherDashboard: TeacherDashboard | null = null;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.teacherDashboard = await RemoteServices.getTeacherDashboard();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped>
.stats-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .items {
    background-color: rgba(255, 255, 255, 0.75);
    color: #1976d2;
    border-radius: 5px;
    flex-basis: 25%;
    margin: 20px;
    cursor: pointer;
    transition: all 0.6s;
  }

  .bar-chart {
    background-color: rgba(255, 255, 255, 0.9);
    height: 400px;
    width: 700px;
    margin: 10px;
  }
}

.icon-wrapper,
.project-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper {
  font-size: 100px;
  transform: translateY(0px);
  transition: all 0.6s;
}

.icon-wrapper {
  align-self: end;
}

.project-name {
  align-self: start;
}

.project-name p {
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 2px;
  transform: translateY(0px);
  transition: all 0.5s;
}

.items:hover {
  border: 3px solid black;

  & .project-name p {
    transform: translateY(-10px);
  }

  & .icon-wrapper i {
    transform: translateY(5px);
  }
}
</style>
