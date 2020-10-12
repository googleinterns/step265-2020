# Resource Discovery Project - Backend

## Adding a new type of asset to be supported:
1. You should create a new class for the new asset and place it in the AssetObjects folder 
1. The class should:
    1. Extend the abstract AssetObject class (which will provide your asset with the following 
       fields: kind, name, id, location, creationTime and status).
    1. **Important** - the name and kind fields most have valid data as they are part of the primary 
        keys in the assets tables.
    1. Implement an inner Builder class which extends the BaseBuilder with the following functions:
        getSpecificClass, getSpecificClassBuilder, constructor and build.
        
        ```java 
                public static class Builder extends BaseBuilder<NewObject, Builder> {
                       /*
                       This function returns a new NewObject.
                        */
                       protected NewObject getSpecificClass() {
                           return new NewObject();
                       }
               
                       /*
                       This function returns this Builder.
                        */
                       protected Builder getSpecificClassBuilder() {
                           return this;
                       }
               
                       /**
                        * This function returns a Builder object for the NewObject class.
                        * @param assetProperties - a Map<String,String> which contains all of the relevant data for
                        *                          this NewObject.
                        * @param projectConfig - the relevant project configurations for this asset.   
                        */
                       public Builder(Map<String,String> assetProperties, ProjectConfig projectConfig) {
                           super(assetProperties, projectConfig);
                       }
               
                       /**
                        * This function sets the relevant fields of the NewObject.
                        * Fields that should be initialized for this object are: 
                        * @return the newly initialized NewObject
                        */
                       public NewObject build() {
                           // Set relevant fields
                           return super.build();
                       }
                   }
        ```
1. Add any asset specific fields to the new asset object class you created and make sure to also
implement public getters for these new fields.
1. Add a new const to the enum AssetKind class by the following convention:
    {SPECIFIC_ASSET_KIND}_{GENERAL_ASSET_KIND}_ASSET (for example: DISK_COMPUTE_ASSET)
    * The 'kind' attribute of the asset objects is added from this enum class. It is provided upon
    the enum initialization and should follow this convention:
        {GENERAL_ASSET_KIND}#{SPECIFIC_ASSET_KIND} (for example: compute#disk)
    Please note that some of the assets have a 'kind' attribute and if so it can be used to set the
    kind for a new asset we support.
           
1. Add its creation to the AssetObjectsFactory (to the createAssetObject function).
1. Add its creation to the getAllAssets function in the ProjectAssetsMapper class.
1. Add a relevant asset table in the spanner db if needed (sometimes there aren't any new interesting
asset attributes which are not covered in the Main_Assets table, and that fine) by following these steps:
    1. For each wanted property of this asset add a new row to the Asset_Tables_Config table in our
    spanner DB. Important guidelines for adding new rows to this configuration table are detailed below. 
    1. In the ProjectMutationsList class you should add a new case statement in the addSpecificAssetMutation
    function and set the new table fields into the relevant Mutation object.

## Spanner DB Tables:
### Adding a new asset table:
Please follow the steps as mentioned in the last section above.
### Updating the structure of an existing asset table:
1. In the AssetTables enum class you should add the relevant fields into the relevant create table query
1. If you added a new field or removed one (and not only changed its type):
    1. In the specific asset kind class (in the com.google.cloudassets.discovery.assetobjects package) you should:
        1. add/remove the field
        1. add/remove its value setting from the Builder.build() function
        1. add a getter function
    1. In the ProjectMutationsList class you should set this field into the relevant Mutation object
1. In order not to have unexpected problems it is best to delete the relevant table manually right 
before running the Main.main function which will created any missing asset tables (granted of course 
that we are not talking about the Main_Assets table as all other asset tables are interleaved
with it and therefore you would have to delete all of them first)
### Asset_Tables_Config configuration table:
When altering or adding a new row the columns which must be filled out for our back-end's use are:
1. assetTableName - the asset table name as will be created in our spanner DB. 
The name most follow this convention: {SPECIFIC_ASSET_KIND}_{GENERAL_ASSET_KIND}_ASSETS
1. assetKind - most match one of the enums of the AssetKind class. Please make sure that each assetKind
has **exactly** one distinct assetTableName.
1. isMainTable - most be set to True for all of the rows of the main asset table and False for any
other row. It is important that **exactly** one table has this field set to True.
1. columnName - a string representing the column name which will be in the asset table and should
represent a property of the asset. 
1. columnType - a string representing the column type in DDL syntax (this string is inserted into the
create table query).
1. isNotNull - set to True if you want a certain column not to enable null values, False otherwise.
1. allowCommitTimestamp - set to True if you want this column to allow commit timestamp insertion (as
we use for the rowLastUpdateTime column), False otherwise.
1. isPrimaryKey - set to True if this column is a primary key in the given table, False otherwise.
Please notice that currently only columns that are marked 'forAllAssets' can be primary keys.
1. primaryKeyIndex - the index of the primary key. Please make sure that there are not two columns
with the same primaryKeyIndex number.