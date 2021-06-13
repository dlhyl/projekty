module.exports = function (sequelize, DataTypes) {
  return sequelize.define(
    "konkurz_restrukturalizacia_actors",
    {
      id: {
        autoIncrement: true,
        type: DataTypes.INTEGER,
        allowNull: false,
        primaryKey: true,
      },
      corporate_body_name: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      cin: {
        type: DataTypes.BIGINT,
        allowNull: true,
      },
      street: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      building_number: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      city: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      postal_code: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      country: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      created_at: {
        type: DataTypes.DATE,
        allowNull: false,
      },
      updated_at: {
        type: DataTypes.DATE,
        allowNull: false,
      },
      company_id: {
        type: DataTypes.BIGINT,
        allowNull: true,
        references: {
          model: "companies",
          key: "cin",
        },
      },
    },
    {
      sequelize,
      tableName: "konkurz_restrukturalizacia_actors",
      schema: "ov",
      timestamps: false,
      indexes: [
        {
          name: "konkurz_restrukturalizacia_actors_company_id",
          fields: [{ name: "company_id" }],
        },
        {
          name: "konkurz_restrukturalizacia_actors_pkey",
          unique: true,
          fields: [{ name: "id" }],
        },
      ],
    }
  );
};
