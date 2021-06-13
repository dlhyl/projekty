module.exports = function (sequelize, DataTypes) {
  return sequelize.define(
    "bulletin_issues",
    {
      id: {
        autoIncrement: true,
        type: DataTypes.INTEGER,
        allowNull: false,
        primaryKey: true,
      },
      year: {
        type: DataTypes.INTEGER,
        allowNull: false,
      },
      number: {
        type: DataTypes.INTEGER,
        allowNull: false,
      },
      published_at: {
        type: DataTypes.DATE,
        allowNull: false,
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
      tableName: "bulletin_issues",
      schema: "ov",
      timestamps: false,
      indexes: [
        {
          name: "bulletin_issues_pkey",
          unique: true,
          fields: [{ name: "id" }],
        },
        {
          name: "index_ov.bulletin_issues_on_updated_at_and_id",
          unique: true,
          fields: [{ name: "updated_at" }, { name: "id" }],
        },
        {
          name: "index_ov.bulletin_issues_on_year_and_number",
          unique: true,
          fields: [{ name: "year" }, { name: "number" }],
        },
      ],
    }
  );
};
