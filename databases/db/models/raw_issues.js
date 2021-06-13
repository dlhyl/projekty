module.exports = function (sequelize, DataTypes) {
  return sequelize.define(
    "raw_issues",
    {
      id: {
        autoIncrement: true,
        type: DataTypes.INTEGER,
        allowNull: false,
        primaryKey: true,
      },
      bulletin_issue_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        references: {
          model: "bulletin_issues",
          key: "id",
        },
      },
      file_name: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      content: {
        type: DataTypes.TEXT,
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
      tableName: "raw_issues",
      schema: "ov",
      timestamps: false,
      indexes: [
        {
          name: "index_ov.raw_issues_on_bulletin_issue_id_and_file_name",
          fields: [{ name: "bulletin_issue_id" }, { name: "file_name" }],
        },
        {
          name: "index_ov.raw_issues_on_updated_at_and_id",
          unique: true,
          fields: [{ name: "updated_at" }, { name: "id" }],
        },
        {
          name: "raw_issues_pkey",
          unique: true,
          fields: [{ name: "id" }],
        },
      ],
    }
  );
};
