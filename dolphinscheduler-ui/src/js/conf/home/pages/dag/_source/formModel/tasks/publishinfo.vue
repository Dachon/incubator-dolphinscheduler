
<template>
  <div class="sql-model">
    <m-list-box>
      <div slot="text">{{$t('Datasource')}}</div>
      <div slot="content">
        <m-datasource
                ref="refDs"
                @on-dsData="_onDsData"
                :data="{ type:type,datasource:datasource }">
        </m-datasource>
      </div>
    </m-list-box>
    <m-list-box>
      <div slot="text">{{$t('SqlTable')}}</div>
      <div slot="content">
       <el-input
         type="input"
         size="small"
         v-model="sqltablename"
         :placeholder="$t('SqlTable TableName')">
       </el-input>
      </div>
    </m-list-box>
    <m-list-box>
      <div slot="text">{{$t('MongoDB')}}</div>
      <div slot="content">
       <el-input
         type="input"
         size="small"
         v-model="mdbtablename"
         :placeholder="$t('MongoDB TableName')">
       </el-input>
      </div>
    </m-list-box>
    <m-list-box>
      <div slot="text">{{$t('ServeName')}}</div>
      <div slot="content">
       <el-input
         type="input"
         size="small"
         v-model="servename"
         :placeholder="$t('ServeName(required)')">
       </el-input>
      </div>
    </m-list-box>
    <m-list-box>
      <div slot="text">{{$t('queryCount')}}</div>
      <div slot="content">
       <el-radio-group v-model="querycount">
          <el-radio :label="'单条'">{{$t('SingleCount')}}</el-radio>
          <el-radio :label="'全部'">{{$t('AllCount')}}</el-radio>
        </el-radio-group>
      </div>
    </m-list-box>
    <m-list-box>
      <div slot="text">{{$t('Custom Parameters')}}</div>
      <div slot="content">
        <m-add-field
            ref="refLocalParams"
            @on-local-params="_onLocalParams"
            :udp-list="localParams"
            :hide="false">
        </m-add-field>
      </div>
    </m-list-box>
  </div>
</template>
<script>
  import _ from 'lodash'
  import i18n from '@/module/i18n'
  import mListBox from './_source/listBox'
  import disabledState from '@/module/mixin/disabledState'
  import mDatasource from './_source/datasource'
  import mAddField from './_source/addfield.vue'

  export default {
    name: 'sql',
    data () {
      return {
        // Sql statement
        sql: '',
        // Data source type
        datasource: '',
        // Return to the selected data source
        rtDatasource: '',
        // Sql statement
        type: '',
        // 关系表存入的 mdbtablename表名
        mdbtablename: '',
        // 关系表存入的服务名
        servename: '',
        // 三方调用服务时返回的数据条数
        querycount: '',
        // 三方SQLtable表名
        sqltablename: '',
        item: '',
        localParams: []
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
        // datasource Subcomponent verification
        if (!this.$refs.refDs._verifDatasource()) {
          return false
        }

        // DB必填
        if (!this.mdbtablename) {
          this.$message.warning(`${i18n.$t('Table name is required')}`)
          return false
        }

        // storage
        this.$emit('on-params', {
          mdbtablename: this.mdbtablename,
          datasource: this.rtDatasource,
          localParams: this.localParams,
          servename: this.servename,
          querycount: this.querycount,
          sqltablename: this.sqltablename
        })
        return true
      },
      /**
       * return data source
       */
      _onDsData (o) {
        this.type = o.type
        this.rtDatasource = o.datasource
      },
      /* return localParams
       */
      _onLocalParams (a) {
        this.localParams = a
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
          mdbtablename: this.mdbtablename,
          datasource: this.rtDatasource,
          localParams: this.localParams,
          servename: this.servename,
          querycount: this.querycount,
          sqltablename: this.sqltablename
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
        this.mdbtablename = o.params.mdbtablename || ''
        this.datasource = o.params.datasource || ''
        this.servename = o.params.servename || ''
        this.querycount = o.params.querycount || ''
        this.sqltablename = o.params.sqltablename || ''
        // backfill localParams
        let localParams = o.params.localParams || []
        if (localParams.length) {
          this.localParams = localParams
        }
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
          mdbtablename: this.mdbtablename,
          datasource: this.rtDatasource,
          localParams: this.localParams,
          servename: this.servename,
          querycount: this.querycount,
          sqltablename: this.sqltablename
        }
      }
    },
    components: { mListBox, mDatasource, mAddField }
  }
</script>
