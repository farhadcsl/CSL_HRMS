<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<#if (requestAttributes.externalLoginKey)?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>
<#if (externalLoginKey)?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>
<#assign ofbizServerName = application.getAttribute("_serverId")?default("default-server")>
<#assign contextPath = request.getContextPath()>
<#assign displayApps = Static["org.ofbiz.base.component.ComponentConfig"].getAppBarWebInfos(ofbizServerName, "main")>

<#if userLogin?has_content>
        <div id="main-nav">
            <h2 class="contracted">${uiLabelMap.CommonApplications}</h2>
            <div id="header-nav" class="clearfix" style="display:none">
                <ul>
                <li><h4>${uiLabelMap.CommonPrimaryApps}</h4></li>
            <#list displayApps as display>
              <#assign thisApp = display.getContextRoot()>
              <#assign permission = true>
              <#assign selected = false>
              <#assign permissions = display.getBasePermission()>
              <#list permissions as perm>
                <#if (perm != "NONE"  && !security.hasEntityPermission(perm, "_VIEW", session) && !authz.hasPermission(session, perm, requestParameters)) >
                        <#-- User must have ALL permissions in the base-permission list -->

                            <#assign permission = false>
                            <!--removed manualy party for all user except super admin-->
                        <#elseif (perm == "PARTYMGR" && security.hasEntityPermission("PARTYMGR", "_VIEW", session) &&  userLogin.userLoginId !="admin")>
                            <#assign permission = false>
                            <!--removed manualy content for all user except super admin-->
                        <#elseif (perm == "CONTENTMGR" && security.hasEntityPermission("CONTENTMGR", "_VIEW", session) &&  userLogin.userLoginId !="admin")>
                            <#assign permission = false>
                        <#elseif (perm == "WORKEFFORTMGR" && security.hasEntityPermission("WORKEFFORTMGR", "_VIEW", session) &&  userLogin.userLoginId !="admin")>
                            <#assign permission = false>
                        <#--contessaAPP_VIEW needed to see the CATALOG app-->
                        <#elseif (perm == "CATALOG" && !security.hasEntityPermission("contessa_CATALOG", "_VIEW", session) &&  userLogin.userLoginId !="admin")>
                            <#assign permission = false>
                        <#elseif (perm == "ORDERMGR" && !security.hasEntityPermission("contessa_ORDERMGR", "_VIEW", session) &&  userLogin.userLoginId !="admin")>
                            <#assign permission = false>
                        <#elseif (perm == "PRODUCTION" && !security.hasEntityPermission("contessa_MANUFACTURING", "_VIEW", session) &&  userLogin.userLoginId !="admin")>
                            <#assign permission = false>
                        <#elseif (perm == "SFA" && !security.hasEntityPermission("contessa_SFA", "_VIEW", session))>
                            <#assign permission = false>
                            <#elseif (perm == "WEBTOOLS" && !security.hasEntityPermission("contessa_WEBTOOLS", "_VIEW", session))>
                            <#assign permission = false>
                        </#if>
              </#list>
              <#if permission == true>
                <#if thisApp == contextPath || contextPath + "/" == thisApp>
                  <#assign selected = true>
                </#if>
                <#assign thisApp = StringUtil.wrapString(thisApp)>
                <#assign thisURL = thisApp>
                <#if thisApp != "/">
                  <#assign thisURL = thisURL + "/control/main">
                </#if>
                  <#if layoutSettings.suppressTab?exists && display.name == layoutSettings.suppressTab>
                    <!-- do not display this component-->
                  <#else>
                    <li><a href="${thisURL + externalKeyParam}" <#if uiLabelMap?exists> title="${uiLabelMap[display.description]}">${uiLabelMap[display.title]}<#else> title="${display.description}">${display.title}</#if></a></li>
                  </#if>
              </#if>
            </#list>
                </ul>
                
                <#include "component://bizznesstime/includes/secondary-appbar.ftl" />
            </div>
        </div>
</#if>