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
      <a-form :form="form" layout="vertical">
        <a-form-item :label="$t('label.name')">
          <a-input v-decorator="['name']" autoFocus />
        </a-form-item>
        <a-form-item :label="$t('label.providername')">
          <a-select
            v-decorator="[
              'provider',
              {
                initialValue: 'NFS'
              }]"
            @change="val => { this.provider = val }"
            showSearch
            optionFilterProp="children"
            :filterOption="(input, option) => {
              return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
            }" >
            <a-select-option
              :value="prov"
              v-for="(prov,idx) in providers"
              :key="idx"
            >{{ prov }}</a-select-option>
          </a-select>
        </a-form-item>
        <div v-if="!['Swift', 'S3'].includes(provider)">
          <a-form-item :label="$t('label.zone')">
            <a-select
              v-decorator="[
                'zone',
                {
                  initialValue: this.zoneSelected,
                  rules: [{ required: true, message: `${$t('label.required')}`}]
                }]"
              @change="val => { zoneSelected = val }"
              showSearch
              optionFilterProp="children"
              :filterOption="(input, option) => {
                return option.componentOptions.propsData.label.toLowerCase().indexOf(input.toLowerCase()) >= 0
              }" >
              <a-select-option
                :value="zone.id"
                v-for="(zone) in zones"
                :key="zone.id"
                :label="zone.name">
                <span>
                  <resource-icon v-if="zone.icon" :image="zone.icon.base64image" size="1x" style="margin-right: 5px"/>
                  <a-icon v-else type="global" style="margin-right: 5px" />
                  {{ zone.name }}
                </span>
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item :label="$t('label.server')">
            <a-input
              v-decorator="[
                'server',
                {
                  rules: [{ required: true, message: `${$t('label.required')}` }]
                }]"
            />
          </a-form-item>
          <a-form-item :label="$t('label.path')">
            <a-input
              v-decorator="[
                'path',
                {
                  rules: [{ required: true, message: `${$t('label.required')}` }]
                }]"
            />
          </a-form-item>
        </div>
        <div v-if="provider === 'SMB/CIFS'">
          <a-form-item :label="$t('label.smbusername')">
            <a-input
              v-decorator="[
                'smbUsername',
                {
                  rules: [{ required: true, message: `${$t('label.required')}` }]
                }]"
            />
          </a-form-item>
          <a-form-item :label="$t('label.smbpassword')">
            <a-input-password
              v-decorator="[
                'smbPassword',
                {
                  rules: [{ required: true, message: `${$t('label.required')}` }]
                }]"
            />
          </a-form-item>
          <a-form-item :label="$t('label.smbdomain')">
            <a-input
              v-decorator="[
                'smbDomain',
                {
                  rules: [{ required: true, message: `${$t('label.required')}` }]
                }]"
            />
          </a-form-item>
        </div>
        <div v-if="provider === 'Swift'">
          <a-form-item :label="$t('label.url')">
            <a-input
              v-decorator="[
                'url',
                {
                  rules: [{ required: true, message: `${$t('label.required')}` }]
                }]"
            />
          </a-form-item>
          <a-form-item :label="$t('label.account')">
            <a-input
              v-decorator="[
                'account',
                {
                  rules: [{ required: true, message: `${$t('label.required')}` }]
                }]"
            />
          </a-form-item>
          <a-form-item :label="$t('label.username')">
            <a-input
              v-decorator="[
                'username',
                {
                  rules: [{ required: true, message: `${$t('label.required')}` }]
                }]"
            />
          </a-form-item>
          <a-form-item :label="$t('label.key')">
            <a-input
              v-decorator="[
                'key',
                {
                  rules: [{ required: true, message: `${$t('label.required')}` }]
                }]"
            />
          </a-form-item>
          <a-form-item :label="$t('label.storagepolicy')">
            <a-input
              v-decorator="[
                'storagepolicy'
              ]"
            />
          </a-form-item>
        </div>
        <div v-if="provider === 'S3'">
          <a-form-item :label="$t('label.zone')">
            <a-select
              v-decorator="[
                'zone',
                {
                  initialValue: this.zoneSelected,
                  rules: [{ required: true, message: `${$t('label.required')}`}]
                }]"
              @change="val => { zoneSelected = val }"
              showSearch
              optionFilterProp="children"
              :filterOption="(input, option) => {
                return option.componentOptions.propsData.label.toLowerCase().indexOf(input.toLowerCase()) >= 0
              }" >
              <a-select-option
                :value="zone.id"
                v-for="(zone) in zones"
                :key="zone.id"
                :label="zone.name">
                <span>
                  <resource-icon v-if="zone.icon" :image="zone.icon.base64image" size="1x" style="margin-right: 5px"/>
                  <a-icon v-else type="global" style="margin-right: 5px" />
                  {{ zone.name }}
                </span>
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item :label="$t('label.s3.access.key')">
            <a-input
              v-decorator="[
                'secondaryStorageAccessKey',
                {
                  rules: [{ required: true, message: `${$t('label.required')}` }]
                }]"/>
          </a-form-item>
          <a-form-item :label="$t('label.s3.secret.key')">
            <a-input
              v-decorator="[
                'secondaryStorageSecretKey',
                {
                  rules: [{ required: true, message: `${$t('label.required')}` }]
                }]"/>
          </a-form-item>
          <a-form-item :label="$t('label.s3.bucket')">
            <a-input
              v-decorator="[
                'secondaryStorageBucket',
                {
                  rules: [{ required: true, message: `${$t('label.required')}` }]
                }]"/>
          </a-form-item>
          <a-form-item :label="$t('label.s3.endpoint')">
            <a-input v-decorator="['secondaryStorageEndpoint']"/>
          </a-form-item>
          <a-form-item :label="$t('label.s3.use.https')">
            <a-switch v-decorator="['secondaryStorageHttps', { initialValue: true }]" :default-checked="true" />
          </a-form-item>
          <a-form-item :label="$t('label.s3.connection.timeout')">
            <a-input v-decorator="['secondaryStorageConnectionTimeout']"/>
          </a-form-item>
          <a-form-item :label="$t('label.s3.max.error.retry')">
            <a-input v-decorator="['secondaryStorageMaxError']"/>
          </a-form-item>
          <a-form-item :label="$t('label.s3.socket.timeout')">
            <a-input v-decorator="['secondaryStorageSocketTimeout']"/>
          </a-form-item>
          <a-form-item :label="$t('label.create.nfs.secondary.staging.storage')">
            <a-switch v-decorator="['secondaryStorageNFSStaging']" @change="val => secondaryStorageNFSStaging = val" />
          </a-form-item>
        </div>
        <div v-if="secondaryStorageNFSStaging">
          <a-form-item :label="$t('label.s3.nfs.server')">
            <a-input
              v-decorator="['secondaryStorageNFSServer', {
                rules: [{ required: true, message: `${$t('label.required')}` }]
              }]"/>
          </a-form-item>
          <a-form-item :label="$t('label.s3.nfs.path')">
            <a-input
              v-decorator="['secondaryStorageNFSPath', {
                rules: [{ required: true, message: `${$t('label.required')}` }]
              }]"/>
          </a-form-item>
        </div>
        <div :span="24" class="action-button">
          <a-button @click="closeModal">{{ $t('label.cancel') }}</a-button>
          <a-button type="primary" ref="submit" @click="handleSubmit">{{ $t('label.ok') }}</a-button>
        </div>
      </a-form>
    </a-spin>
  </div>
</template>
<script>
import { api } from '@/api'
import ResourceIcon from '@/components/view/ResourceIcon'

export default {
  name: 'AddSecondryStorage',
  props: {
    resource: {
      type: Object,
      required: true
    }
  },
  components: {
    ResourceIcon
  },
  inject: ['parentFetchData'],
  data () {
    return {
      providers: ['NFS', 'SMB/CIFS', 'S3', 'Swift'],
      provider: '',
      zones: [],
      zoneSelected: '',
      loading: false,
      secondaryStorageNFSStaging: false
    }
  },
  beforeCreate () {
    this.form = this.$form.createForm(this)
  },
  created () {
    this.fetchData()
  },
  methods: {
    fetchData () {
      this.listZones()
    },
    closeModal () {
      this.$parent.$parent.close()
    },
    listZones () {
      api('listZones', { showicon: true }).then(json => {
        if (json && json.listzonesresponse && json.listzonesresponse.zone) {
          this.zones = json.listzonesresponse.zone
          if (this.zones.length > 0) {
            this.zoneSelected = this.zones[0].id || ''
          }
        }
      })
    },
    nfsURL (server, path) {
      var url
      if (path.substring(0, 1) !== '/') {
        path = '/' + path
      }
      if (server.indexOf('://') === -1) {
        url = 'nfs://' + server + path
      } else {
        url = server + path
      }
      return url
    },
    smbURL (server, path, smbUsername, smbPassword, smbDomain) {
      var url = ''
      if (path.substring(0, 1) !== '/') {
        path = '/' + path
      }
      if (server.indexOf('://') === -1) {
        url += 'cifs://'
      }
      url += (server + path)
      return url
    },
    handleSubmit (e) {
      e.preventDefault()
      if (this.loading) return
      this.form.validateFieldsAndScroll(async (err, values) => {
        if (err) {
          return
        }

        var data = {
          name: values.name
        }
        var url = ''
        var provider = values.provider
        if (provider === 'NFS') {
          url = this.nfsURL(values.server, values.path)
        }
        if (provider === 'SMB/CIFS') {
          provider = 'SMB'
          url = this.smbURL(values.server, values.path, values.smbUsername, values.smbPassword, values.smbDomain)
          const smbParams = {
            user: values.smbUsername,
            password: values.smbPassword,
            domain: values.smbDomain
          }
          Object.keys(smbParams).forEach((key, index) => {
            data['details[' + index.toString() + '].key'] = key
            data['details[' + index.toString() + '].value'] = smbParams[key]
          })
        }
        if (provider === 'Swift') {
          url = values.url
          const swiftParams = {
            account: values.account,
            username: values.username,
            key: values.key,
            storagepolicy: values.storagepolicy
          }
          Object.keys(swiftParams).forEach((key, index) => {
            data['details[' + index.toString() + '].key'] = key
            data['details[' + index.toString() + '].value'] = swiftParams[key]
          })
        } else if (provider === 'S3') {
          let detailIdx = 0
          const s3Params = {
            accesskey: values.secondaryStorageAccessKey,
            secretkey: values.secondaryStorageSecretKey,
            bucket: values.secondaryStorageBucket,
            usehttps: values.secondaryStorageHttps ? values.secondaryStorageHttps : false
          }
          Object.keys(s3Params).forEach((key, index) => {
            data['details[' + index.toString() + '].key'] = key
            data['details[' + index.toString() + '].value'] = s3Params[key]
            detailIdx = index
          })

          if (values.secondaryStorageEndpoint && values.secondaryStorageEndpoint.length > 0) {
            detailIdx++
            data['details[' + detailIdx.toString() + '].key'] = 'endpoint'
            data['details[' + detailIdx.toString() + '].value'] = values.secondaryStorageEndpoint
          }

          if (values.secondaryStorageConnectionTimeout && values.secondaryStorageConnectionTimeout.length > 0) {
            detailIdx++
            data['details[' + detailIdx.toString() + '].key'] = 'connectiontimeout'
            data['details[' + detailIdx.toString() + '].value'] = values.secondaryStorageConnectionTimeout
          }

          if (values.secondaryStorageMaxError && values.secondaryStorageMaxError.length > 0) {
            detailIdx++
            data['details[' + detailIdx.toString() + '].key'] = 'maxerrorretry'
            data['details[' + detailIdx.toString() + '].value'] = values.secondaryStorageMaxError
          }

          if (values.secondaryStorageSocketTimeout && values.secondaryStorageSocketTimeout.length > 0) {
            detailIdx++
            data['details[' + detailIdx.toString() + '].key'] = 'sockettimeout'
            data['details[' + detailIdx.toString() + '].value'] = values.secondaryStorageSocketTimeout
          }
        }

        if (provider !== 'S3') {
          data.url = url
        }
        data.provider = provider
        if (values.zone && !['Swift', 'S3'].includes(provider)) {
          data.zoneid = values.zone
        }

        const nfsParams = {}
        if (values.secondaryStorageNFSStaging) {
          const nfsServer = values.secondaryStorageNFSServer
          const path = values.secondaryStorageNFSPath
          const nfsUrl = this.nfsURL(nfsServer, path)

          nfsParams.provider = 'nfs'
          nfsParams.zoneid = values.zone
          nfsParams.url = nfsUrl
        }

        this.loading = true

        try {
          await this.addImageStore(data)

          if (values.secondaryStorageNFSStaging) {
            await this.createSecondaryStagingStore(nfsParams)
          }
          this.$notification.success({
            message: this.$t('label.add.secondary.storage'),
            description: this.$t('label.add.secondary.storage')
          })
          this.loading = false
          this.closeModal()
          this.parentFetchData()
        } catch (error) {
          this.$notifyError(error)
          this.loading = false
        }
      })
    },
    addImageStore (params) {
      return new Promise((resolve, reject) => {
        api('addImageStore', params).then(json => {
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },
    createSecondaryStagingStore (params) {
      return new Promise((resolve, reject) => {
        api('createSecondaryStagingStore', params).then(json => {
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.form-layout {
  width: 85vw;

  @media (min-width: 1000px) {
    width: 35vw;
  }
}
</style>
