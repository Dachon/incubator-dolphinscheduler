
<template>
  <div class="sql-model">
    <m-list-box>
      <div slot="text">{{$t('ServeName')}}</div>
      <div slot="content">
       <el-input
         type="input"
         size="small"
         v-model="serviceName"
         :placeholder="$t('ServeName(required)')">
       </el-input>
      </div>
    </m-list-box>
  </div>
</template>
<script>
  import _ from 'lodash'
  import mListBox from './_source/listBox'
  import disabledState from '@/module/mixin/disabledState'

  export default {
    name: 'sql',
    data () {
      return {
        // Sql statement
        sql: '',
        // Sql statement
        // 关系表存入的服务名
        serviceName: '',
        // 三方调用服务时返回的数据条数
        item: ''
      }
    },
    mixins: [disabledState],
    props: {
      backfillItem: Object,
      createNodeId: Number
    },
    methods: {
      /**
       * verification
       */
      _verification () {
        // storage
        this.$emit('on-params', {
          serviceName: this.serviceName
        })
        return true
      },
      _getReceiver () {
        let param = {}
        let current = this.router.history.current
        if (current.name === 'projects-definition-details') {
          param.processDefinitionId = current.params.id
        } else {
          param.processInstanceId = current.params.id
        }
      },
      _cacheParams () {
        this.$emit('on-cache-params', {
          serviceName: this.serviceName
        })
      }
    },
    watch: {
      // Watch the cacheParams
      cacheParams (val) {
        this._cacheParams()
      }
    },
    created () {
      let o = this.backfillItem

      // Non-null objects represent backfill
      if (!_.isEmpty(o)) {
        this.serviceName = o.params.serviceName || ''
      }
      // read tasks from cache
      if (!_.some(this.store.state.dag.cacheTasks, { id: this.createNodeId }) &&
        this.router.history.current.name !== 'definition-create') {
        this._getReceiver()
      }
    },
    mounted () {

    },
    destroyed () {

    },
    computed: {
      cacheParams () {
        return {
          serviceName: this.serviceName
        }
      }
    },
    components: { mListBox }
  }
</script>
