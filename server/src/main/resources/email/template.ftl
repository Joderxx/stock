<html>
<head>
  <style>
      table {
          width: 50%;
          margin: 0 auto
      }

      thead tr th {
          font-weight: bold;
          background-color: #5f87e0;
          color: whitesmoke;
          padding: 6px;
      }

      tbody tr:hover {
          background-color: rgba(0, 0, 0, 0.1);
      }

      tbody tr td {
          text-align: center;
          color: #262626;
          padding: 6px;
          border-bottom: 1px solid rgba(0, 0, 0, 0.3);
      }
  </style>
</head>
<body>
<h3>日期: ${date!''}</h3>
<h4>类型: ${type!''}</h4>
<#if data?exists>
  <table cellspacing="0">
      <#if titles?exists>
        <thead>
        <tr>
            <#list titles?keys as k>
              <th>${titles[k]}</th>
            </#list>
        </tr>
        </thead>
      </#if>
    <tbody>
    <#list data as item>
      <tr>
          <#list titles?keys as k>
            <td>${item[k]}</td>
          </#list>
      </tr>
    </#list>
    </tbody>
  </table>
</#if>
<p>${extend!''}</p>
</body>
</html>