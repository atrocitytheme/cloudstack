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
      <a-form :form="form" :loading="loading" @submit="handleSubmit" layout="vertical">
        <a-form-item>
          <tooltip-label slot="label" :title="$t('label.username')" :tooltip="apiParams.username.description"/>
          <a-input
            v-decorator="['username', {
              rules: [{ required: true, message: $t('message.error.required.input') }]
            }]"
            :placeholder="apiParams.username.description"
            autoFocus />
        </a-form-item>
        <a-form-item>
          <tooltip-label slot="label" :title="$t('label.email')" :tooltip="apiParams.email.description"/>
          <a-input
            v-decorator="['email', {
              rules: [{ required: true, message: $t('message.error.required.input') }]
            }]"
            :placeholder="apiParams.email.description" />
        </a-form-item>
        <a-row :gutter="12">
          <a-col :md="24" :lg="12">
            <a-form-item>
              <tooltip-label slot="label" :title="$t('label.firstname')" :tooltip="apiParams.firstname.description"/>
              <a-input
                v-decorator="['firstname', {
                  rules: [{ required: true, message: $t('message.error.required.input') }]
                }]"
                :placeholder="apiParams.firstname.description" />
            </a-form-item>
          </a-col>
          <a-col :md="24" :lg="12">
            <a-form-item>
              <tooltip-label slot="label" :title="$t('label.lastname')" :tooltip="apiParams.lastname.description"/>
              <a-input
                v-decorator="['lastname', {
                  rules: [{ required: true, message: $t('message.error.required.input') }]
                }]"
                :placeholder="apiParams.lastname.description" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item>
          <tooltip-label slot="label" :title="$t('label.timezone')" :tooltip="apiParams.timezone.description"/>
          <a-select
            v-decorator="['timezone']"
            :loading="timeZoneLoading"
            showSearch
            optionFilterProp="children"
            :filterOption="(input, option) => {
              return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
            }" >
            <a-select-option v-for="opt in timeZoneMap" :key="opt.id">
              {{ opt.name || opt.description }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <div :span="24" class="action-button">
          <a-button @click="closeAction">{{ $t('label.cancel') }}</a-button>
          <a-button :loading="loading" ref="submit" type="primary" @click="handleSubmit">{{ $t('label.ok') }}</a-button>
        </div>
      </a-form>
    </a-spin>
  </div>
</template>

<script>
import { api } from '@/api'
import { timeZone } from '@/utils/timezone'
import debounce from 'lodash/debounce'
import TooltipLabel from '@/components/widgets/TooltipLabel'

export default {
  name: 'EditUser',
  components: {
    TooltipLabel
  },
  props: {
    resource: {
      type: Object,
      required: true
    },
    currentAction: {
      type: Object,
      required: true
    }
  },
  data () {
    this.fetchTimeZone = debounce(this.fetchTimeZone, 800)
    return {
      loading: false,
      timeZoneLoading: false,
      timeZoneMap: [],
      userId: null
    }
  },
  beforeCreate () {
    this.form = this.$form.createForm(this)
    this.apiParams = this.$getApiParams('updateUser')
  },
  created () {
    this.fetchData()
  },
  methods: {
    fetchData () {
      this.userId = this.$route.params.id || null
      this.fetchTimeZone()
      this.fillEditFormFieldValues()
    },
    fetchTimeZone (value) {
      this.timeZoneMap = []
      this.timeZoneLoading = true

      timeZone(value).then(json => {
        this.timeZoneMap = json
        this.timeZoneLoading = false
      })
    },
    fillEditFormFieldValues () {
      const form = this.form
      this.loading = true
      Object.keys(this.apiParams).forEach(item => {
        const field = this.apiParams[item]
        let fieldValue = null
        let fieldName = null

        if (field.type === 'list' || field.name === 'account') {
          fieldName = field.name.replace('ids', 'name').replace('id', 'name')
        } else {
          fieldName = field.name
        }
        fieldValue = this.resource[fieldName] ? this.resource[fieldName] : null
        if (fieldValue) {
          form.getFieldDecorator(field.name, { initialValue: fieldValue })
        }
      })
      this.loading = false
    },
    isValidValueForKey (obj, key) {
      return key in obj && obj[key] != null
    },
    handleSubmit (e) {
      e.preventDefault()
      if (this.loading) return
      this.form.validateFieldsAndScroll((err, values) => {
        if (err) {
          return
        }
        this.loading = true
        const params = {
          id: this.userId,
          username: values.username,
          email: values.email,
          firstname: values.firstname,
          lastname: values.lastname
        }
        if (this.isValidValueForKey(values, 'timezone') && values.timezone.length > 0) {
          params.timezone = values.timezone
        }

        api('updateUser', params).then(response => {
          this.$emit('refresh-data')
          this.$notification.success({
            message: this.$t('label.edit.user'),
            description: `${this.$t('message.success.update.user')} ${params.username}`
          })
          this.closeAction()
        }).catch(error => {
          this.$notification.error({
            message: this.$t('message.request.failed'),
            description: (error.response && error.response.headers && error.response.headers['x-description']) || error.message,
            duration: 0
          })
        }).finally(() => {
          this.loading = false
        })
      })
    },
    closeAction () {
      this.$emit('close-action')
    }
  }
}
</script>

<style scoped lang="less">
.form-layout {
  width: 80vw;
  @media (min-width: 600px) {
    width: 450px;
  }
}
</style>
