<template>
  <div class="user-def-params-model">
    <div class="select-listpp"
         v-for="(item,$index) in localParamsList"
         :key="item.id"
         @click="_getIndex($index)">
      <el-input
              :disabled="isDetails"
              type="text"
              size="small"
              v-model="localParamsList[$index].sqlfiled"
              :placeholder="$t('sql(required)')"
              maxlength="256"
              @blur="_verifProp()"
              :style="inputStyle">
      </el-input>
      <el-input
              :disabled="isDetails"
              type="text"
              size="small"
              v-model="localParamsList[$index].mdbfiled"
              :placeholder="$t('mdb(required)')"
              maxlength="256"
              @blur="_handleValue()"
              :style="inputStyle">
      </el-input>
      <span class="lt-add">
        <a href="javascript:" style="color:red;" @click="!isDetails && _removeUdp($index)" >
          <em class="el-icon-delete" :class="_isDetails" data-toggle="tooltip" :title="$t('delete')" ></em>
        </a>
      </span>
      <span class="add" v-if="$index === (localParamsList.length - 1)">
        <a href="javascript:" @click="!isDetails && _addUdp()" >
          <em class="el-icon-circle-plus-outline" :class="_isDetails" data-toggle="tooltip" :title="$t('Add')"></em>
        </a>
      </span>
    </div>
    <span class="add-dp" v-if="!localParamsList.length">
      <a href="javascript:" @click="!isDetails && _addUdp()" >
        <em class="iconfont el-icon-circle-plus-outline" :class="_isDetails" data-toggle="tooltip" :title="$t('Add')"></em>
      </a>
    </span>
  </div>
</template>
<script>
  import _ from 'lodash'
  import i18n from '@/module/i18n'
  import { directList, typeList } from './commcon'
  import disabledState from '@/module/mixin/disabledState'
  export default {
    name: 'user-def-params',
    data () {
      return {
        // Direct data Custom parameter type support IN
        directList: directList,
        // Type data Custom parameter type support OUT
        typeList: typeList,
        // Increased data
        localParamsList: [],
        // Current execution index
        localParamsIndex: null
      }
    },
    mixins: [disabledState],
    props: {
      udpList: Array,
      // hide direct/type
      hide: {
        type: Boolean,
        default: true
      }
    },
    methods: {
      /**
       * Current index
       */
      _getIndex (index) {
        this.localParamsIndex = index
      },
      /**
       * handle direct
       */
      _handleDirectChanged () {
        this._verifProp('value')
      },
      /**
       * handle type
       */
      _handleTypeChanged () {
        this._verifProp('value')
      },
      /**
       * delete item
       */
      _removeUdp (index) {
        this.localParamsList.splice(index, 1)
        this._verifProp('value')
      },
      /**
       * add
       */
      _addUdp () {
        this.localParamsList.push({
          sqlfiled: '',
          mdbfiled: ''
        })
      },
      /**
       * blur verification
       */
      _handleValue (type) {
        let arr = []
        let flag = true
        _.map(this.localParamsList, v => {
          arr.push(v.mdbfiled)
          if (!v.mdbfiled) {
            flag = false
          }
        })
        if (!flag) {
          if (!type) {
            this.$message.warning(`${i18n.$t('mdbfiled is empty')}`)
          }
          return false
        }
        let newArr = _.cloneDeep(_.uniqWith(arr, _.isEqual))
        if (newArr.length !== arr.length) {
          if (!type) {
            this.$message.warning(`${i18n.$t('mdbfiled is repeat')}`)
          }
          return false
        }

        this.$emit('on-local-params', _.cloneDeep(this.localParamsList))
        return true
      },
      /**
       * Verify that the value exists or is empty
       */
      _verifProp (type) {
        let arr = []
        let flag = true
        _.map(this.localParamsList, v => {
          arr.push(v.sqlfiled)
          if (!v.sqlfiled) {
            flag = false
          }
        })
        if (!flag) {
          if (!type) {
            this.$message.warning(`${i18n.$t('sqlfiled is empty')}`)
          }
          return false
        }
        let newArr = _.cloneDeep(_.uniqWith(arr, _.isEqual))
        if (newArr.length !== arr.length) {
          if (!type) {
            this.$message.warning(`${i18n.$t('sqlfiled is repeat')}`)
          }
          return false
        }

        this.$emit('on-local-params', _.cloneDeep(this.localParamsList))
        return true
      }
    },
    watch: {
      // Monitor data changes
      udpList () {
        this.localParamsList = this.udpList
      }
    },
    created () {
      this.localParamsList = this.udpList
    },
    computed: {
      inputStyle () {
        return `width:${this.hide ? 160 : 252}px`
      }
    },
    mounted () {
    },
    components: { }
  }
</script>

<style lang="scss" rel="stylesheet/scss">
  .user-def-params-model {
    .select-listpp {
      margin-bottom: 6px;
      .lt-add {
        padding-left: 4px;
        line-height: 32px;
        a {
          .iconfont, [class^="el-icon"] {
            font-size: 17px;
            vertical-align: middle;
            display: inline-block;
            margin-top: 0;
          }
        }
      }
    }
    .add {
      line-height: 32px;
      a {
        color: #000;
        .iconfont, [class^="el-icon"] {
          font-size: 18px;
          vertical-align: middle;
          display: inline-block;
          margin-top: 0;
        }
      }
    }
    .add-dp {
      a {
        color: #0097e0;
        .iconfont, [class^="el-icon"] {
          font-size: 18px;
          vertical-align: middle;
          display: inline-block;
          margin-top: 2px;
        }
      }
    }
  }
</style>
