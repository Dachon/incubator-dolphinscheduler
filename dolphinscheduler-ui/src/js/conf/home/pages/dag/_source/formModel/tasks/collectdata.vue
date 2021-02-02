
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
      <div slot="text">{{$t('SQL Type')}}</div>
      <div slot="content">
        <div style="display: inline-block;">
         <!-- <m-sql-type
                  @on-sqlType="_onSqlType"
                  :sql-type="sqlType">
          </m-sql-type> -->
          <el-select
                  v-model="sqlType"
                  :disabled="true"
                  style="width: 160px;"
                  size="small">
          </el-select>
        </div>
      </div>
    </m-list-box>
    <m-list-box>
      <div slot="text">{{$t('SQL Statement')}}</div>
      <div slot="content">
        <div class="from-mirror">
          <textarea
                  id="code-sql-mirror"
                  name="code-sql-mirror"
                  style="opacity: 0;">
          </textarea>
          <a class="ans-modal-box-max">
            <em class="el-icon-full-screen" @click="setEditorVal"></em>
          </a>
        </div>
      </div>
    </m-list-box>
    <el-dialog
      title="SQL输入"
      :visible.sync="scriptBoxDialog"
      append-to-body="true"
      width="80%">
      <m-script-box :item="item" @getSriptBoxValue="getSriptBoxValue" @closeAble="closeAble"></m-script-box>
    </el-dialog>
  </div>
</template>
<script>
  import _ from 'lodash'
  import i18n from '@/module/i18n'
  import mListBox from './_source/listBox'
  import disabledState from '@/module/mixin/disabledState'
  import codemirror from '@/conf/home/pages/resource/pages/file/pages/_source/codemirror'
  import mScriptBox from './_source/scriptBox'
  import mDatasource from './_source/datasource'
  let editor

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
        sqlType: '查询',
        // Email title
        item: '',
        scriptBoxDialog: false
      }
    },
    mixins: [disabledState],
    props: {
      backfillItem: Object,
      createNodeId: Number
    },
    methods: {
      setEditorVal () {
        this.item = editor.getValue()
        this.scriptBoxDialog = true
      },
      getSriptBoxValue (val) {
        editor.setValue(val)
      },
      /**
       * verification
       */
      _verification () {
        if (!editor.getValue()) {
          this.$message.warning(`${i18n.$t('Please enter a SQL Statement(required)')}`)
          return false
        }

        // datasource Subcomponent verification
        if (!this.$refs.refDs._verifDatasource()) {
          return false
        }

        this.$emit('on-params', {
          sql: editor.getValue(),
          datasource: this.rtDatasource,
          sqlType: this.sqlType,
          type: this.type
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
      /**
       * Processing code highlighting
       */
      _handlerEditor () {
        this._destroyEditor()

        // editor
        editor = codemirror('code-sql-mirror', {
          mode: 'sql',
          readOnly: this.isDetails
        })

        this.keypress = () => {
          if (!editor.getOption('readOnly')) {
            editor.showHint({
              completeSingle: false
            })
          }
        }

        this.changes = () => {
          this._cacheParams()
        }

        // Monitor keyboard
        editor.on('keypress', this.keypress)

        editor.on('changes', this.changes)

        editor.setValue(this.sql)

        return editor
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
          sql: editor ? editor.getValue() : '',
          datasource: this.rtDatasource,
          sqlType: this.sqlType,
          type: this.type
        })
      },
      _destroyEditor () {
        if (editor) {
          editor.toTextArea() // Uninstall
          editor.off($('.code-sql-mirror'), 'keypress', this.keypress)
          editor.off($('.code-sql-mirror'), 'changes', this.changes)
        }
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
        // backfill
        this.sql = o.params.sql || ''
        this.datasource = o.params.datasource || ''
        this.sqlType = o.params.sqlType || ''
        this.type = o.params.type || ''
      }
      // read tasks from cache
      if (!_.some(this.store.state.dag.cacheTasks, { id: this.createNodeId }) &&
        this.router.history.current.name !== 'definition-create') {
        this._getReceiver()
      }
    },
    mounted () {
      setTimeout(() => {
        this._handlerEditor()
      }, 200)
    },
    destroyed () {
      /**
       * Destroy the editor instance
       */
      if (editor) {
        editor.toTextArea() // Uninstall
        editor.off($('.code-sql-mirror'), 'keypress', this.keypress)
        editor.off($('.code-sql-mirror'), 'changes', this.changes)
      }
    },
    computed: {
      cacheParams () {
        return {
          datasource: this.rtDatasource,
          sqlType: this.sqlType,
          sql: this.sql,
          type: this.type
        }
      }
    },
    components: { mListBox, mScriptBox, mDatasource }
  }
</script>
