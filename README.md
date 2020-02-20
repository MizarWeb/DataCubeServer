# DataCubeServer

DataCubeServer is a java application that reads cube files and returns the their data in json following to the [DataCube](https://github.com/MizarWeb/DataCube) app according to its [API](https://github.com/MizarWeb/DataCube/blob/master/SERVER/README.md).

| ![DataCube operation with DataCubeServer](https://github.com/MizarWeb/DataCube/wiki/images/DatacubeServer_en.png)| 
|:--:| 
|  _DataCube operation with DataCubeServer_ |

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
