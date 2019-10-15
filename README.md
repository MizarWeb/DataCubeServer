# DataCubeServer

## Installation 
```
git clone https://github.com/MizarWeb/DataCubeServer.git
cd DataCubeServer
vim cubeExplorer.properties and set the same <path> (eg : /tmp/tests/) for workspace and workspace_cube
	also edit the dimX,dimY,and dimZ with the dimention names available in your cubes.
mkdir <path>/private
mkdir <path>/public>
mvn install
java -jar target/cubeExplorer-1.0.0-SNAPSHOT.jar
```
## API server documentation

1. Identification (API to connect user) : Method POST with two values
http://localhost:8081/cubeExplorer/rest/identification
JSON : {login:<login_value>;password:<password_calue>}

2. List Files (API return all files availables : files conatining into private and public folders): Method GET
http://localhost:8081/cubeExplorer/rest/listFiles

3. Get slide of cube (following parameter return metadata and values to calcultate lat/long)
3.1 Parameter entry : input file (with extension)
3.2 Parameter metadata : 
    - if metadata parameter is not present, api return all metadatas of file.
    - if metata parameter is present with empty value, api return feature containing all properties 
http://localhost:8081/cubeExplorer/rest/header?entry=<file>&metadata=<metadata_value>

4. Get slide of cube
4.1 Parameter entry : input file (with extension)
4.2 Parameter posZ : slide number
http://localhost:8081/cubeExplorer/rest/slide?entry=<file>&posZ=<slideNumber>

5. Get spectre values : Method GET
5.1 Parameter entry : input file (with extension)
5.2 Parameter metadata : NAXIS.
5.3 Parameter posX : coordinate X
5.4 Parameter posY : coordinate Y
http://localhost:8081/cubeExplorer/rest/spectrum?entry=<file>&metadata=NAXIS.&posX=<x>&posY=<y>
