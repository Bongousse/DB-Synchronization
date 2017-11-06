// Setting
var outputDdlFile = newFile("/db.sync/ddl/ddl.txt"); // 출력 DDL 파일 경로
var sourceDbmsType = 0; // 원본 파일 DBMS 타입 0: ORACLE, 1: POSTGRESQL, 2: MYSQL
var outputDbmsType = 1; // 출력 파일 DBMS 타입 0: ORACLE, 1: POSTGRESQL, 2: MYSQL
var generatePrimaryKey = true; // PK 생성 옵션
var generateComment = true; // 코멘트 생성 옵션

// DBMS 도메인 타입
var stringArray = newList();
stringArray.add("VARCHAR2"); // ORACLE
stringArray.add("VARCHAR"); // POSTGRESQL
stringArray.add("VARCHAR"); // MYSQL

var numberArray = newList();
numberArray.add("NUMBER"); // ORACLE
numberArray.add("NUMERIC"); // POSTGRESQL
numberArray.add("INT"); // MYSQL

var decimalArray = newList();
decimalArray.add("NUMBER"); // ORACLE
dceimalArray.add("NUMERIC"); // POSTGRESQL
dceimalArray.add("FLOAT"); // MYSQL

var binaryArray = newList();
binaryArray.add("BLOB"); // ORACLE
binaryArray.add("BYTEA"); // POSTGRESQL
binaryArray.add("BLOB"); // MYSQL

var charBinaryArray = newList();
charBinaryArray.add("CLOB"); // ORACLE
charBinaryArray.add("BYTEA"); // POSTGRESQL
charBinaryArray.add("BINARY"); // MYSQL

var outputStream = outputDdlFile.getOutputStream();

select(function(it){
	return it.get("type") == "table";
}).each(function(table){
	
	var tableName = table.get("physical-name");
	
	// CREATE TABLE
	console.log(format("CREATE TABLE %s (", tableName));
	outputStream.println(format("CREATE TABLE %s (", tableName));
	
	var numberOfColumns = table.select(function(it){
		return it.get("type") == "column";
	}).size();
	
	var index = 0;
	var primaryKeyCount = 0;
	
	// 컬럼 출력
	table.select(function(it){
		return it.get("type") == "column";
	}).each(function(column){
		index++;
		
		var indent = "\t";
		
		var physicalName = column.get("physical-name");
		var dataType = column.get("data-type");
		
		var dataTypeWithoutSize;
		if (dataType.contains('(')){
			dataTypeWithoutSize = dataType.substring(0, dataType.indexOf('('));
		} else {
			dataTypeWithoutSize = dataType;
		}
		
		var suffix = "\\s*((\\([^)]*\\))?)";
		var ori = dataType;
		var p = compilePattern("^" + dataTypeWithoutSize + suffix + "$");
		var matcher = p.matcher(dataType);
		if (matcher.matches()){
			if (stringArray.contains(dataTypeWithoutSize)){
				dataType = stringArray.get(outputDbmsType) + matcher.group(1);
			} else if (numberArray.contains(dataTypeWithoutSize)){
				dataType = numberArray.get(outputDbmsType) + matcher.group(1);
			} else if (binaryArray.contains(dataTypeWithoutSize)){
				dataType = binaryArray.get(outputDbmsType) + matcher.group(1);
			}
		}
		
		var nullable = column.get("null-exp");
		var isPrimaryKey = column.get("is-primary-key");
		if (isPrimaryKey == true){
			primaryKeyCount++;
		}
		
		// MYSQL의 경우 컬럼 출력 시 comment 출력
		var extra;
		if (generateComment == true && outputDbmsType == 2){
			var comment = column.get("logical-name");
			extra = "COMMENT '" + comment + "'";
		} else {
			extra = "";
		}
		
		if (index === numberOfColumns){
			console.log(format("%s %s %s %s %s", indent, physicalName, dataType, nullable, extra));
			outputStream.println(format("%s %s %s %s %s", indent, physicalName, dataType, nullable, extra));
		} else {
			console.log(format("%s %s %s %s %s,", indent, physicalName, dataType, nullable, extra));
			outputStream.println(format("%s %s %s %s %s,", indent, physicalName, dataType, nullable, extra));
		}
	});
	
	// MYSQL의 경우 테이블 출력 시 테이블 comment 출력
	if (generateComment == true && outputDbmsType == 2){
		var comment = table.get("logical-name");
		console.log(format(") COMMENT '%s';\n", comment));
		outputStream.println(format(") COMMENT '%s';\n", comment));
	} else {
		console.log(format(");\n"));
		outputStream.println(format(");\n"));
	}
	
	// PK 생성
	if (generatePrimaryKey == true){
		
		// ORACLE or MYSQL
		if (outputDbmsType == 0 || outputDbmsType == 2){
			console.log(format("ALTER TABLE %s ADD CONSTRAINT PK_%s PRIMARY KEY \n( ", tableName, tableName));
			outputStream.println(format("ALTER TABLE %s ADD CONSTRAINT PK_%s PRIMARY KEY \n( ", tableName, tableName));
			
			index = 0;
			table.select(function(it){
				return it.get("type") == "column";
			}).each(function(column){
				index++;
				
				var indent = "\t";
				
				var physicalName = column.get("physical-name");
				var isPrimaryKey = column.get("is-primary-key");
				var primaryKey;
				if (isPrimaryKey == true){
					if (index === primaryKeyCount){
						console.log(format("%s %s\n);", indent, physicalName));
						outputStream.println(format("%s %s\n);", indent, physicalName));
					} else {
						console.log(format("%s %s, ", indent, physicalName));
						outputStream.println(format("%s %s, ", indent, physicalName));
					}
				}
			});
			
			console.log(format("\n"));
			outputStream.println(format("\n"));
		}
		
		// POSTGRESQL
		else if (outputDbmsType == 1){
			console.log(format("CREATE UNIQUE INDEX PK_%s ON %s \n( ", tableName, tableName));
			outputStream.println(format("CREATE UNIQUE INDEX PK_%s ON %s \n( ", tableName, tableName));
			
			var index = 0;
			table.select(function(it){
				return it.get("type") == "column";
			}).each(function(column){
				index++;
				
				var indent = "\t";
				
				var physicalName = column.get("physical-name");
				var isPrimaryKey = column.get("is-primary-key");
				var primaryKey;
				if (isPrimaryKey == true){
					
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
	}
	
	// 코멘트 생성
	if (generateComment == true && (outputDbmsType == 0 || outputDbmsType == 1)){
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