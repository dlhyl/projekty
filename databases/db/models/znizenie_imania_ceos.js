module.exports = function (sequelize, DataTypes) {
  return sequelize.define(
    "znizenie_imania_ceos",
    {
      id: {
        autoIncrement: true,
        type: DataTypes.INTEGER,
        allowNull: false,
        primaryKey: true,
      },
      znizenie_imania_issue_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        references: {
          model: "znizenie_imania_issues",
          key: "id",
        },
      },
      prefixes: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      postfixes: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      given_name: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      family_name: {
        type: DataTypes.STRING,
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
      postal_code: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      city: {
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
    },
    {
      sequelize,
      tableName: "znizenie_imania_ceos",
      schema: "ov",
      timestamps: false,
      indexes: [
        {
          name: "index_ov.znizenie_imania_ceos_on_znizenie_imania_issue_id",
          fields: [{ name: "znizenie_imania_issue_id" }],
        },
        {
          name: "znizenie_imania_ceos_pkey",
          unique: true,
          fields: [{ name: "id" }],
        },
      ],
    }
  );
};
