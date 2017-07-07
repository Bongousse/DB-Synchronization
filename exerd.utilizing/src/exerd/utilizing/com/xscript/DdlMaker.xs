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
	
	console.log(format("CREATE TABLE %s (", table.get("physical-name")));
	outputStream.println(format("CREATE TABLE %s (", table.get("physical-name")));
	
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
		
		if (index === total){
			console.log(format("%s %s %s %s", indent, physicalName, dataType, nullable));
			outputStream.println(format("%s %s %s %s", indent, physicalName, dataType, nullable));
		} else {
			console.log(format("%s %s %s %s,", indent, physicalName, dataType, nullable));
			outputStream.println(format("%s %s %s %s,", indent, physicalName, dataType, nullable));
		}
		
	});
	
	console.log(format(");"));
	console.log();
	
	outputStream.println(format(");"));
	outputStream.println();
});

outputStream.close();