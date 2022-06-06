

for(role in roles) {
  if(role.roleTypeId=="DIVISION"){
   context.isDivision=true;
  }else if(role.roleTypeId=="DEPARTMENT"){
   context.isDeparment=true;
  }else if(role.roleTypeId=="SECTION"){
   context.isSection=true;
  }else if(role.roleTypeId=="UNIT"){
   context.isLine=true;
  }
 }