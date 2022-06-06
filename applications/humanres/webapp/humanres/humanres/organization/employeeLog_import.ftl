  <div id="partyContent" class="screenlet">
    <div class="screenlet-title-bar">
      <ul>
        <li class="h3">Attendance Employee Log Import </li>
      </ul>
      <br class="clear" />
    </div>
     
    <div class="screenlet-body">
          
      <div class="label"> </div>
      <form id="uploadPartyContent" method="post" enctype="multipart/form-data" onsubmit="return validateForm()" action="<@ofbizUrl>uploadEmployeeLog</@ofbizUrl>">
    
        <input type="file" name="filename"/>
        <input type="submit" value="${uiLabelMap.CommonUpload}" />
      </form>
  
       <hr />
      
    </div>
  </div>

  <script type="text/javascript">
  function validateForm() {
  
    var x = document.forms["uploadPartyContent"]["filename"].value;
    if (x == null || x == "") {
        alert("Please Select a File");
        return false;
    }
}
  
  </script>



