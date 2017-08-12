plugins.addGetter("null-exp", function(it){
	if (it.get("not-null") == true){
		return "NOT NULL";
	} else {
		return "NULL";
	}
});

var file = newFile("/exerd.utilizing/bxm_ddl.txt");
var outputStream = file.getOutputStream();

var isPrimaryKeyOption = true;
var isCommentOption = true;	
	
select(function(it){
	return it.get("type") == "table";
}).each(function(table){
	
	var tableName = table.get("physical-name");  
	
	// CREATE TABLE
	console.log(format("CREATE TABLE %s (", tableName));
	outputStream.println(format("CREATE TABLE %s (", tableName));
	
	var total = table.select(function(it){
		return it.get("type") == "column";
	}).size();
	
	var index = 0;
	var primaryKeyCount = 0;
	
	table.select(function(it){
		return it.get("type") == "column";
	}).each(function(column){ 
		index ++;
		
		var indent = "\t";
		
		var physicalName = column.get("physical-name");
		var dataType = column.get("data-type");
		var nullable = column.get("null-exp");
		var isPrimaryKey = column.get("is-primary-key");
		if(isPrimaryKey == true){
			primaryKeyCount ++; 
		} 
		  
		if (index === total){
			console.log(format("%s %s %s %s", indent, physicalName, dataType, nullable));
			outputStream.println(format("%s %s %s %s", indent, physicalName, dataType, nullable));
		} else {
			console.log(format("%s %s %s %s,", indent, physicalName, dataType, nullable));
			outputStream.println(format("%s %s %s %s,", indent, physicalName, dataType, nullable));
		}
	});
	
	console.log(format(");\n"));
	
	outputStream.println(format(");\n"));
	
	var index = 0;
	// PK 생성
	if(isPrimaryKeyOption == true){
		console.log(format("CREATE UNIQUE INDEX PK_%s ON %s \n( ", tableName, tableName));
		outputStream.println(format("CREATE UNIQUE INDEX PK_%s ON %s \n( ", tableName, tableName));
		
		table.select(function(it){
			return it.get("type") == "column";
		}).each(function(column){
			index ++;
			
			var indent = "\t";
			
			var physicalName = column.get("physical-name");
			var isPrimaryKey = column.get("is-primary-key");
			var primaryKey;
			if(isPrimaryKey == true){
				
				if (index === primaryKeyCount){
					console.log(format("%s %s ASC", indent, physicalName));
					outputStream.println(format("%s %s ASC", indent, physicalName));
				} else {
					console.log(format("%s %s ASC,", indent, physicalName));
					outputStream.println(format("%s %s ASC,", indent, physicalName));
				}
			} 
		});
		
		console.log(format(");\n"));
		outputStream.println(format(");\n"));
		
		console.log(format("ALTER TABLE %s ADD CONSTRAINT PK_%s PRIMARY KEY USING INDEX PK_%s;\n", tableName, tableName, tableName));
		outputStream.println(format("ALTER TABLE %s ADD CONSTRAINT PK_%s PRIMARY KEY USING INDEX PK_%s;\n", tableName, tableName, tableName));
	}

	// 코멘트 생성
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