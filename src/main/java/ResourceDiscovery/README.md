# Resource Discovery Project - Backend

## Adding a new type of asset to be supported:
1. You should create a new class for the new asset and place it in the AssetObjects folder 
1. The class should:
    1. Extend the abstract AssetObject class (which will provide your asset with the following 
       fields: kind, name, id, type, zone, creationTime and status).
    1. Implement an inner Builder class which extends the BaseBuilder with the following functions:
        getSpecificClass, getSpecificClassBuilder, constructor and build.
        <addr> public static class Builder extends BaseBuilder<NewObject, Builder> {
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
                        * @param assetObjectsMap - a Map<String,String> which contains all of the relevant data for
                        *                          this NewObject.
                        */
                       public Builder(Map<String,String> assetObjectsMap) {
                           super(assetObjectsMap);
                       }
               
                       /**
                        * This function sets the relevant fields of the NewObject.
                        * Fields that should be initialized for this object are: 
                        * @return the newly initialized NewObject
                        */
                       public NewObject build() {
                           // set relevant fields
                           return super.build();
                       }
                   }
        </addr>
1. Add its creation to the AssetObjectsFactory (to the createAssetObject function).
1. Add its creation to the getAllAssets function in the Main class.

