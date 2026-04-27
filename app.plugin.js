const { createRunOncePlugin } = require("@expo/config-plugins");

const pkg = require("./package.json");

const withGainsightPX = config => config;

module.exports = createRunOncePlugin(withGainsightPX, pkg.name, pkg.version);
