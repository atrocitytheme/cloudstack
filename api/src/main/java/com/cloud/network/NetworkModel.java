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

package com.cloud.network;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cloud.dc.Vlan;
import com.cloud.exception.InsufficientAddressCapacityException;
import com.cloud.exception.InvalidParameterValueException;
import com.cloud.hypervisor.Hypervisor.HypervisorType;
import com.cloud.network.Network.Capability;
import com.cloud.network.Network.IpAddresses;
import com.cloud.network.Network.Provider;
import com.cloud.network.Network.Service;
import com.cloud.network.Networks.TrafficType;
import com.cloud.network.element.NetworkElement;
import com.cloud.network.element.UserDataServiceProvider;
import com.cloud.offering.NetworkOffering;
import com.cloud.offering.NetworkOffering.Detail;
import com.cloud.user.Account;
import com.cloud.vm.Nic;
import com.cloud.vm.NicProfile;
import com.cloud.vm.VirtualMachine;
import org.apache.cloudstack.framework.config.ConfigKey;

/**
 * The NetworkModel presents a read-only view into the Network data such as L2 networks,
 * Nics, PublicIps, NetworkOfferings, traffic labels, physical networks and the like
 * The idea is that only the orchestration core should be able to modify the data, while other
 * participants in the orchestration can use this interface to query the data.
 */
public interface NetworkModel {
    String METATDATA_DIR = "metadata";
    String USERDATA_DIR = "userdata";
    String USERDATA_FILE = "user_data";
    String PASSWORD_DIR = "password";
    String PASSWORD_FILE = "vm_password";
    String PASSWORD_CHECKSUM_FILE = "vm-password-md5checksum";
    String SERVICE_OFFERING_FILE = "service-offering";
    String AVAILABILITY_ZONE_FILE = "availability-zone";
    String LOCAL_HOSTNAME_FILE = "local-hostname";
    String LOCAL_IPV4_FILE = "local-ipv4";
    String PUBLIC_HOSTNAME_FILE = "public-hostname";
    String PUBLIC_IPV4_FILE = "public-ipv4";
    String INSTANCE_ID_FILE = "instance-id";
    String VM_ID_FILE = "vm-id";
    String PUBLIC_KEYS_FILE = "public-keys";
    String CLOUD_IDENTIFIER_FILE = "cloud-identifier";
    String HYPERVISOR_HOST_NAME_FILE = "hypervisor-host-name";
    int CONFIGDATA_DIR = 0;
    int CONFIGDATA_FILE = 1;
    int CONFIGDATA_CONTENT = 2;
    ImmutableMap<String, String> openStackFileMapping = ImmutableMap.of(
            AVAILABILITY_ZONE_FILE, "availability_zone",
            LOCAL_HOSTNAME_FILE, "hostname",
            VM_ID_FILE, "uuid",
            PUBLIC_HOSTNAME_FILE, "name"
    );

    static final ConfigKey<Integer> MACIdentifier = new ConfigKey<Integer>("Advanced",Integer.class, "mac.identifier", "0",
            "This value will be used while generating the mac addresses for isolated and shared networks. The hexadecimal equivalent value will be present at the 2nd octet of the mac address. Default value is null which means this feature is disabled.Its scope is global.", true, ConfigKey.Scope.Global);

    /**
     * Lists IP addresses that belong to VirtualNetwork VLANs
     *
     * @param accountId
     *            - account that the IP address should belong to
     * @param associatedNetworkId
     *            TODO
     * @param sourceNat
     *            - (optional) true if the IP address should be a source NAT address
     * @return - list of IP addresses
     */
    List<? extends IpAddress> listPublicIpsAssignedToGuestNtwk(long accountId, long associatedNetworkId, Boolean sourceNat);

    List<? extends IpAddress> listPublicIpsAssignedToGuestNtwk(long associatedNetworkId, Boolean sourceNat);

    List<? extends NetworkOffering> getSystemAccountNetworkOfferings(String... offeringNames);

    List<? extends Nic> getNics(long vmId);

    String getNextAvailableMacAddressInNetwork(long networkConfigurationId) throws InsufficientAddressCapacityException;

    PublicIpAddress getPublicIpAddress(long ipAddressId);

    List<? extends Vlan> listPodVlans(long podId);

    List<? extends Network> listNetworksUsedByVm(long vmId, boolean isSystem);

    default List<Long> listNetworksUsedByVm(long vmId) {
        throw new UnsupportedOperationException();
    }

    Nic getNicInNetwork(long vmId, long networkId);

    List<? extends Nic> getNicsForTraffic(long vmId, TrafficType type);

    Network getDefaultNetworkForVm(long vmId);

    Nic getDefaultNic(long vmId);

    UserDataServiceProvider getUserDataUpdateProvider(Network network);

    boolean networkIsConfiguredForExternalNetworking(long zoneId, long networkId);

    Map<Capability, String> getNetworkServiceCapabilities(long networkId, Service service);

    boolean isSharedNetworkWithoutServices(long networkId);

    boolean areServicesSupportedByNetworkOffering(long networkOfferingId, Service... services);

    Network getNetworkWithSGWithFreeIPs(Long zoneId);

    Network getNetworkWithSecurityGroupEnabled(Long zoneId);

    String getIpOfNetworkElementInVirtualNetwork(long accountId, long dataCenterId);

    List<? extends Network> listNetworksForAccount(long accountId, long zoneId, Network.GuestType type);

    List<? extends Network> listAllNetworksInAllZonesByType(Network.GuestType type);

    String getStartIpAddress(long networkId);

    String getIpInNetwork(long vmId, long networkId);

    String getIpInNetworkIncludingRemoved(long vmId, long networkId);

    Long getPodIdForVlan(long vlanDbId);

    List<Long> listNetworkOfferingsForUpgrade(long networkId);

    boolean isSecurityGroupSupportedInNetwork(Network network);

    boolean isProviderSupportServiceInNetwork(long networkId, Service service, Provider provider);

    boolean isProviderEnabledInPhysicalNetwork(long physicalNetowrkId, String providerName);

    String getNetworkTag(HypervisorType hType, Network network);

    List<Service> getElementServices(Provider provider);

    boolean canElementEnableIndividualServices(Provider provider);

    boolean areServicesSupportedInNetwork(long networkId, Service... services);

    boolean isNetworkSystem(Network network);

    Map<Capability, String> getNetworkOfferingServiceCapabilities(NetworkOffering offering, Service service);

    Long getPhysicalNetworkId(Network network);

    boolean getAllowSubdomainAccessGlobal();

    boolean isProviderForNetwork(Provider provider, long networkId);

    boolean isProviderForNetworkOffering(Provider provider, long networkOfferingId);

    void canProviderSupportServices(Map<Provider, Set<Service>> providersMap);

    List<PhysicalNetworkSetupInfo> getPhysicalNetworkInfo(long dcId, HypervisorType hypervisorType);

    boolean canAddDefaultSecurityGroup();

    List<Service> listNetworkOfferingServices(long networkOfferingId);

    boolean areServicesEnabledInZone(long zoneId, NetworkOffering offering, List<Service> services);

    Map<PublicIpAddress, Set<Service>> getIpToServices(List<? extends PublicIpAddress> publicIps, boolean rulesRevoked, boolean includingFirewall);

    Map<Provider, ArrayList<PublicIpAddress>> getProviderToIpList(Network network, Map<PublicIpAddress, Set<Service>> ipToServices);

    boolean checkIpForService(IpAddress ip, Service service, Long networkId);

    boolean providerSupportsCapability(Set<Provider> providers, Service service, Capability cap);

    void checkCapabilityForProvider(Set<Provider> providers, Service service, Capability cap, String capValue);

    Provider getDefaultUniqueProviderForService(String serviceName);

    void checkNetworkPermissions(Account owner, Network network);

    String getDefaultManagementTrafficLabel(long zoneId, HypervisorType hypervisorType);

    String getDefaultStorageTrafficLabel(long zoneId, HypervisorType hypervisorType);

    String getDefaultPublicTrafficLabel(long dcId, HypervisorType vmware);

    String getDefaultGuestTrafficLabel(long dcId, HypervisorType vmware);

    /**
     * @param providerName
     * @return
     */
    NetworkElement getElementImplementingProvider(String providerName);

    /**
     * @param accountId
     * @param zoneId
     * @return
     */
    String getAccountNetworkDomain(long accountId, long zoneId);

    /**
     * @param ntwkOffId
     * @return
     */
    List<Provider> getNtwkOffDistinctProviders(long ntwkOffId);

    /**
     * @param accountId
     * @param dcId
     * @param sourceNat
     * @return
     */
    List<? extends IpAddress> listPublicIpsAssignedToAccount(long accountId, long dcId, Boolean sourceNat);

    /**
     * @param zoneId
     * @param trafficType
     * @return
     */
    List<? extends PhysicalNetwork> getPhysicalNtwksSupportingTrafficType(long zoneId, TrafficType trafficType);

    /**
     * @param ntwkId
     * @return
     */
    boolean isPrivateGateway(long ntwkId);

    Map<Service, Map<Capability, String>> getNetworkCapabilities(long networkId);

    Network getSystemNetworkByZoneAndTrafficType(long zoneId, TrafficType trafficType);

    Long getDedicatedNetworkDomain(long networkId);

    Map<Service, Set<Provider>> getNetworkOfferingServiceProvidersMap(long networkOfferingId);

    List<? extends Provider> listSupportedNetworkServiceProviders(String serviceName);

    List<? extends Network> listNetworksByVpc(long vpcId);

    boolean canUseForDeploy(Network network);

    Network getExclusiveGuestNetwork(long zoneId);

    long findPhysicalNetworkId(long zoneId, String tag, TrafficType trafficType);

    Integer getNetworkRate(long networkId, Long vmId);

    boolean isVmPartOfNetwork(long vmId, long ntwkId);

    PhysicalNetwork getDefaultPhysicalNetworkByZoneAndTrafficType(long zoneId, TrafficType trafficType);

    Network getNetwork(long networkId);

    IpAddress getIp(long sourceIpAddressId);

    boolean isNetworkAvailableInDomain(long networkId, long domainId);

    NicProfile getNicProfile(VirtualMachine vm, long networkId, String broadcastUri);

    Set<Long> getAvailableIps(Network network, String requestedIp);

    String getDomainNetworkDomain(long domainId, long zoneId);

    PublicIpAddress getSourceNatIpAddressForGuestNetwork(Account owner, Network guestNetwork);

    boolean isNetworkInlineMode(Network network);

    boolean areThereIPv6AddressAvailableInNetwork(long networkId);

    boolean isIP6AddressAvailableInVlan(long vlanId);

    void checkIp6Parameters(String startIPv6, String endIPv6, String ip6Gateway, String ip6Cidr) throws InvalidParameterValueException;

    void checkRequestedIpAddresses(long networkId, IpAddresses ips) throws InvalidParameterValueException;

    String getStartIpv6Address(long id);

    boolean isProviderEnabledInZone(long zoneId, String provider);

    Nic getPlaceholderNicForRouter(Network network, Long podId);

    IpAddress getPublicIpAddress(String ipAddress, long zoneId);

    List<String> getUsedIpsInNetwork(Network network);

    Map<Detail, String> getNtwkOffDetails(long offId);

    Networks.IsolationType[] listNetworkIsolationMethods();

    Nic getNicInNetworkIncludingRemoved(long vmId, long networkId);

    boolean getExecuteInSeqNtwkElmtCmd();

    boolean isNetworkReadyForGc(long networkId);

    boolean getNetworkEgressDefaultPolicy(Long networkId);

    List<String[]> generateVmData(String userData, String serviceOffering, long datacenterId,
                                  String vmName, String vmHostName, long vmId, String vmUuid, String guestIpAddress, String publicKey, String password, Boolean isWindows, String hostname);

    String getValidNetworkCidr(Network guestNetwork);

}
