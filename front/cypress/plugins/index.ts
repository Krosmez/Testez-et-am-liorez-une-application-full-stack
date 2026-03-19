/**
 * @type {Cypress.PluginConfig}
 */

import registerCodeCoverageTasks from '@cypress/code-coverage/task';

const pluginConfig: Cypress.PluginConfig = (on, config) => {
  return registerCodeCoverageTasks(on, config);
};

export default pluginConfig;
