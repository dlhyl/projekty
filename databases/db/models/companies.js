module.exports = function (sequelize, DataTypes) {
  return sequelize.define(
    "companies",
    {
      cin: {
        type: DataTypes.BIGINT,
        allowNull: false,
        primaryKey: true,
      },
      name: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      br_section: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      address_line: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      last_update: {
        type: DataTypes.DATE,
        allowNull: true,
      },
      created_at: {
        type: DataTypes.DATE,
        allowNull: true,
      },
      updated_at: {
        type: DataTypes.DATE,
        allowNull: true,
      },
    },
    {
      sequelize,
      tableName: "companies",
      schema: "ov",
      timestamps: false,
      indexes: [
        {
          name: "companies_pkey",
          unique: true,
          fields: [{ name: "cin" }],
        },
      ],
    }
  );
};
