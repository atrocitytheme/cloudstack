// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

<template>
  <div class="form-layout" v-ctrl-enter="handleSubmit">
    <a-spin :spinning="loading">
      <a-form
        :form="form"
        @submit="handleSubmit"
        layout="vertical">
        <a-form-item>
          <tooltip-label slot="label" :title="$t('label.name')" :tooltip="apiParams.name.description"/>
          <a-input
            v-decorator="['name', {
              rules: [{ required: true, message: $t('message.error.required.input') }]
            }]"
            :placeholder="apiParams.name.description"
            autoFocus />
        </a-form-item>

        <a-form-item>
          <tooltip-label slot="label" :title="$t('label.description')" :tooltip="apiParams.description.description"/>
          <a-input
            v-decorator="['description']"
            :placeholder="apiParams.description.description" />
        </a-form-item>

        <a-form-item v-if="'roleid' in apiParams">
          <tooltip-label slot="label" :title="$t('label.based.on')" :tooltip="$t('label.based.on.role.id.or.type')"/>
          <a-radio-group
            v-decorator="['using', {
              initialValue: this.createRoleUsing
            }]"
            buttonStyle="solid"
            @change="selected => { this.handleChangeCreateRole(selected.target.value) }">
            <a-radio-button value="type">
              {{ $t('label.type') }}
            </a-radio-button>
            <a-radio-button value="role">
              {{ $t('label.role') }}
            </a-radio-button>
          </a-radio-group>
        </a-form-item>

        <a-form-item v-if="this.createRoleUsing === 'type'">
          <tooltip-label slot="label" :title="$t('label.type')" :tooltip="apiParams.type.description"/>
          <a-select
            v-decorator="['type', {
              rules: [{ required: true, message: $t('message.error.select') }]
            }]"
            :placeholder="apiParams.type.description"
            showSearch
            optionFilterProp="children"
            :filterOption="(input, option) => {
              return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
            }" >
            <a-select-option v-for="role in defaultRoles" :key="role">
              {{ role }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item v-if="this.createRoleUsing === 'role'">
          <tooltip-label slot="label" :title="$t('label.role')" :tooltip="apiParams.roleid.description"/>
          <a-select
            v-decorator="['roleid', {
              rules: [{ required: true, message: $t('message.error.select') }]
            }]"
            :placeholder="apiParams.roleid.description"
            showSearch
            optionFilterProp="children"
            :filterOption="(input, option) => {
              return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
            }" >
            <a-select-option
              v-for="role in roles"
              :value="role.id"
              :key="role.id">
              {{ role.name }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <div :span="24" class="action-button">
          <a-button @click="closeAction">{{ this.$t('label.cancel') }}</a-button>
          <a-button :loading="loading" ref="submit" type="primary" @click="handleSubmit">{{ this.$t('label.ok') }}</a-button>
        </div>
      </a-form>
    </a-spin>
  </div>
</template>

<script>
import { api } from '@/api'
import TooltipLabel from '@/components/widgets/TooltipLabel'

export default {
  name: 'CreateRole',
  components: {
    TooltipLabel
  },
  data () {
    return {
      roles: [],
      defaultRoles: ['Admin', 'DomainAdmin', 'User'],
      createRoleUsing: 'type',
      loading: false
    }
  },
  created () {
    this.fetchRoles()
  },
  beforeCreate () {
    this.form = this.$form.createForm(this)
    this.apiParams = this.$getApiParams('createRole')
  },
  watch: {
    '$route' (to, from) {
      if (to.fullPath !== from.fullPath && !to.fullPath.includes('action/')) {
        this.fetchRoles()
      }
    },
    '$i18n.locale' (to, from) {
      if (to !== from) {
        this.fetchRoles()
      }
    }
  },
  methods: {
    handleSubmit (e) {
      e.preventDefault()
      if (this.loading) return
      this.form.validateFieldsAndScroll((err, values) => {
        if (err) {
          return
        }
        const params = {}
        for (const key in values) {
          if (key === 'using') {
            continue
          }

          const input = values[key]
          if (input === undefined) {
            continue
          }

          params[key] = input
        }

        this.createRole(params)
      })
    },
    closeAction () {
      this.$emit('close-action')
    },
    createRole (params) {
      this.loading = true
      api('createRole', params).then(json => {
        const role = json.createroleresponse.role
        if (role) {
          this.$emit('refresh-data')
          this.$notification.success({
            message: 'Create Role',
            description: 'Sucessfully created role ' + params.name
          })
        }
        this.closeAction()
      }).catch(error => {
        this.$notifyError(error)
      }).finally(() => {
        this.loading = false
      })
    },
    fetchRoles () {
      const params = {}
      api('listRoles', params).then(json => {
        if (json && json.listrolesresponse && json.listrolesresponse.role) {
          this.roles = json.listrolesresponse.role
        }
      }).catch(error => {
        console.error(error)
      })
    },
    handleChangeCreateRole (value) {
      this.createRoleUsing = value
    }
  }
}
</script>

<style scoped lang="less">
  .form-layout {
    width: 80vw;

    @media (min-width: 700px) {
      width: 550px;
    }
  }
</style>
