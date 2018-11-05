<#import "parts/common.ftl" as c>
<@c.page>
Изменение пользователей:
<form method="post" action="/user">
    <input type="text" name="userName" value="${user.username}">
    <#list roles as role>
        <div>
            <label><input type="checkbox" name="${role}" ${user.roles?seq_contains(role)?string("checked", "")}/>${role}</label>
        </div>
    </#list>
    <input type="hidden" name="userId" value="${user.id}">
    <input type="hidden" name="_csrf" value="${_csrf.token}"/>
    <button type="submit">Сохранить</button>
</form>
</@c.page>