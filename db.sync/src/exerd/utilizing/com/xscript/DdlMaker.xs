// Setting
var outputDdlFile = newFile("/DB-Synchronization/db.sync/ddl/ddl.txt"); // 출력 DDL 파일 경로
var sourceDbmsType = 0; // 원본 파일 DBMS 타입 0: ORACLE, 1: POSTGRESQL, 2: MYSQL
var outputDbmsType = 2; // 출력 파일 DBMS 타입 0: ORACLE, 1: POSTGRESQL, 2: MYSQL
var generatePrimaryKey = true; // PK 생성 옵션
var generateComment = true; // 코멘트 생성 옵션
var generateDiagram = true; // 다이어그램 출력 옵션

// DBMS 도메인 타입
var stringArray = newList(); // 문자열
stringArray.add("VARCHAR2"); // ORACLE
stringArray.add("VARCHAR"); // POSTGRESQL
stringArray.add("VARCHAR"); // MYSQL

var numberArray = newList(); // 8 바이트 이하 정수
numberArray.add("NUMBER"); // ORACLE
numberArray.add("NUMERIC"); // POSTGRESQL
numberArray.add("INT"); // MYSQL

var bigNumberArray = newList(); // 9 바이트 이상 정수
bigNumberArray.add("NUMBER"); // ORACLE
bigNumberArray.add("NUMERIC"); // POSTGRESQL
bigNumberArray.add("BIGINT"); // MYSQL

var decimalArray = newList(); // 소수
decimalArray.add("NUMBER"); // ORACLE
decimalArray.add("NUMERIC"); // POSTGRESQL
decimalArray.add("FLOAT"); // MYSQL

var binaryArray = newList(); // 이진 데이터
binaryArray.add("BLOB"); // ORACLE
binaryArray.add("BYTEA"); // POSTGRESQL
binaryArray.add("LONGBLOB"); // MYSQL 

var charBinaryArray = newList(); // 캐릭터 이진 데이터
charBinaryArray.add("CLOB"); // ORACLE
charBinaryArray.add("BYTEA"); // POSTGRESQL
charBinaryArray.add("TEXT"); // MYSQL

var outputStream = outputDdlFile.getOutputStream();

plugins.addGetter("null-exp", function(it){
	if (it.get("not-null") == true){
		return "NOT NULL";
	} else {
		return "NULL";
	}
});

select(function(it){
	return it.get("type") == "table";
}).each(function(table){
	
	if (generateDiagram){
		var tableDiagram = table.get("table-diagram");
		tableDiagram.each(function(it){
			console.log(format("--%s", it.get("diagram")));
			outputStream.println(format("--%s", it.get("diagram")));
		});
	}
	
	var tableName = table.get("physical-name");
	
	// CREATE TABLE
	console.log(format("CREATE TABLE %s (", tableName));
	outputStream.println(format("CREATE TABLE %s (", tableName));
	
	var numberOfColumns = table.select(function(it){
		return it.get("type") == "column";
	}).size();
	
	var index = 0;
	var primaryKeyCount = 0;
	var primaryKeyList = newList();
	
	// 컬럼 출력
	table.select(function(it){
		return it.get("type") == "column";
	}).each(function(column){
		index++;
		
		var indent = "\t";
		
		var physicalName = column.get("physical-name");
		var dataType = column.get("data-type");
		
		var lengthRegex = "((\\([^)]*\\))?)";
		var dataTypeWithoutSize = dataType.replaceAll(lengthRegex, "");
		
		var suffix = "\\s*((\\([^)]*\\))?)";
		var ori = dataType;
		var p = compilePattern("^" + dataTypeWithoutSize + suffix + "$");
		var matcher = p.matcher(dataType);
		if (matcher.matches()){
			if (stringArray.contains(dataTypeWithoutSize)){
				dataType = stringArray.get(outputDbmsType) + matcher.group(1);
			} else if (numberArray.contains(dataTypeWithoutSize)){
				if (dataType.contains(',')){
					dataType = decimalArray.get(outputDbmsType) + matcher.group(1);
				} else {
					// 데이터 타입에 사이즈가 있을 때 처리
					if (dataType.contains('(')){
						var size = matcher.group(1).substring(matcher.group(1).indexOf('(') + 1, matcher.group(1).indexOf(')'));
						if (size <= 8){
							dataType = numberArray.get(outputDbmsType);
						} else {
							dataType = bigNumberArray.get(outputDbmsType);
						}
						
						// MYSQL은 정수 타입에 사이즈를 붙이지 않음
						if (outputDbmsType != 2){
							dataType += matcher.group(1)
						}
					} else {
						dataType = bigNumberArray.get(outputDbmsType);
					}
				}
			} else if (bigNumberArray.contains(dataTypeWithoutSize)){
				dataType = bigNumberArray.get(outputDbmsType);
				// MYSQL은 정수 타입에 사이즈를 붙이지 않음
				if (outputDbmsType != 2){
					dataType += matcher.group(1)
				}
			} else if (decimalArray.contains(dataTypeWithoutSize)){
				dataType = decimalArray.get(outputDbmsType) + matcher.group(1);
			} else if (binaryArray.contains(dataTypeWithoutSize)){
				dataType = binaryArray.get(outputDbmsType) + matcher.group(1);
			} else if (charBinaryArray.contains(dataTypeWithoutSize)){
				dataType = charBinaryArray.get(outputDbmsType) + matcher.group(1);
			}
		}
		
		var nullable = column.get("null-exp");
		var isPrimaryKey = column.get("is-primary-key");
		if (isPrimaryKey == true){
			primaryKeyCount++;
			primaryKeyList.add(physicalName);
		}
		
		var extra;
		var defaultValue = column.get("default-value");
		if (defaultValue != null){
			extra = "DEFAULT " + defaultValue + " ";
		} else {
			extra = "";
		}
		
		// MYSQL의 경우 컬럼 출력 시 comment 출력
		if (generateComment == true && outputDbmsType == 2){
			var comment = column.get("logical-name");
			if (comment != null){
				extra += "COMMENT '" + comment + "'";
			}
		} else {
			extra = extra;
		}
		
		if (index === numberOfColumns && primaryKeyList.size() != 0){
			//MYSQL의 경우 CREATE TABLE 안에서 PK 생성
			if (outputDbmsType == 2 && generatePrimaryKey){
				console.log(format("%s %s %s %s %s,", indent, physicalName, dataType, nullable, extra));
				outputStream.println(format("%s %s %s %s %s,", indent, physicalName, dataType, nullable, extra));
				
				console.logf(format("%s PRIMARY KEY(", indent));
				outputStream.printf(format("%s PRIMARY KEY(", indent));
				
				for (var i = 0; i < primaryKeyList.size(); i++){
					if (i == primaryKeyList.size() - 1){
						console.log(format("%s) ", primaryKeyList.get(i)));
						outputStream.println(format("%s) ", primaryKeyList.get(i)));
					} else {
						console.logf(format("%s, ", primaryKeyList.get(i)));
						outputStream.printf(format("%s, ", primaryKeyList.get(i)));
					}
				}
			} else {
				console.log(format("%s %s %s %s %s", indent, physicalName, dataType, nullable, extra));
				outputStream.println(format("%s %s %s %s %s", indent, physicalName, dataType, nullable, extra));
			}
		} else {
			console.log(format("%s %s %s %s %s,", indent, physicalName, dataType, nullable, extra));
			outputStream.println(format("%s %s %s %s %s,", indent, physicalName, dataType, nullable, extra));
		}
	});
	
	// MYSQL의 경우 테이블 출력 시 테이블 comment 출력
	if (generateComment == true && outputDbmsType == 2){
		var comment = table.get("logical-name");
		if (comment != null){
			console.log(format(") COMMENT '%s';\n", comment));
			outputStream.println(format(") COMMENT '%s';\n", comment));
		}
	} else {
		console.log(format(");\n"));
		outputStream.println(format(");\n"));
	}
	
	// PK 생성
	if (generatePrimaryKey == true && primaryKeyList.size() != 0){
		
		// ORACLE
		if (outputDbmsType == 0){
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
		if (comment != null){
			console.log(format("COMMENT ON TABLE %s IS '%s';", tableName, comment));
			outputStream.println(format("COMMENT ON TABLE %s IS '%s';", tableName, comment));
		}
		
		table.select(function(it){
			return it.get("type") == "column";
		}).each(function(column){
			var physicalName = column.get("physical-name");
			var comment = column.get("logical-name");
			if (comment != null){
				console.log(format("COMMENT ON COLUMN %s.%s IS '%s';", tableName, physicalName, comment));
				outputStream.println(format("COMMENT ON COLUMN %s.%s IS '%s';", tableName, physicalName, comment));
			}
		});
	}
	console.log();
	outputStream.println();
});

outputStream.close();