plugins.addGetter("null-exp", function(it){
	if (it.get("not-null") == true){
		return "NOT NULL";
	} else {
		return "NULL";
	}
});

var file = newFile("/exerd.utilizing/ddl.txt");
var outputStream = file.getOutputStream();
	
select(function(it){
	return it.get("type") == "table";
}).each(function(table){
	
	var tableName = table.get("physical-name");  
	
	console.log(format("CREATE TABLE %s (", tableName));
	outputStream.println(format("CREATE TABLE %s (", tableName));
	
	var total = table.select(function(it){
		return it.get("type") == "column";
	}).size();
	
	var index = 0;
	
	table.select(function(it){
		return it.get("type") == "column";
	}).each(function(column){
		index ++;
		
		var indent = "\t";
		
		var physicalName = column.get("physical-name");
		var dataType = column.get("data-type");
		var nullable = column.get("null-exp");
		var isPrimaryKey = column.get("is-primary-key");
		var primaryKey;
		if(isPrimaryKey == true){ 
			primaryKey = "primary key"; 
		} else {  
			primaryKey = "";
		}
		  
		if (index === total){
			console.log(format("%s %s %s %s %s", indent, physicalName, dataType, nullable, primaryKey));
			outputStream.println(format("%s %s %s %s %s", indent, physicalName, dataType, nullable, primaryKey));
		} else {
			console.log(format("%s %s %s %s %s,", indent, physicalName, dataType, nullable, primaryKey));
			outputStream.println(format("%s %s %s %s %s,", indent, physicalName, dataType, nullable, primaryKey));
		}
	});
	
	console.log(format(");"));
	console.log();
	
	outputStream.println(format(");"));
	outputStream.println();

	var isCommentOption = true;	
	if(isCommentOption == true){
		var comment = table.get("logical-name");
		console.log(format("COMMENT ON TABLE %s IS '%s';", tableName, comment));
		outputStream.println(format("COMMENT ON TABLE %s IS '%s';", tableName, comment));
		
		table.select(function(it){
			return it.get("type") == "column";
		}).each(function(column){
			var physicalName = column.get("physical-name");
			var comment = column.get("logical-name");
			console.log(format("COMMENT ON COLUMN %s.%s IS '%s';", tableName, physicalName, comment));
			outputStream.println(format("COMMENT ON COLUMN %s.%s IS '%s';", tableName, physicalName, comment));
		});
	}				
	console.log();
	outputStream.println();
});

outputStream.close();