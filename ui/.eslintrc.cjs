module.exports = {
    root: true,
    env: {
        browser: true,
        node: true,
    },
    parserOptions: {
        parser: '@typescript-eslint/parser',
        ecmaVersion: 2021,
        sourceType: 'module',
    },
    extends: [
        'eslint:recommended',
        'plugin:@typescript-eslint/recommended',
        'plugin:vue/vue3-recommended',
    ],
    rules: {
        semi: ['error', 'never'], // Enforce no semicolons
        quotes: ['error', 'single'], // Enforce single quotes
        'no-unused-vars': 'warn', // Warn about unused variables
        'no-console': 'warn', // Warn about console.log statements
        'vue/order-in-components': ['error', { // Enforce order in Vue components
            order: [
                'el',
                'name',
                'key',
                'parent',
                'functional',
                ['delimiters', 'comments'],
                ['components', 'directives', 'filters'],
                'extends',
                'mixins',
                'inheritAttrs',
                'model',
                ['props', 'propsData'],
                'emits',
                'setup',
                'asyncData',
                'data',
                'computed',
                'watch',
                'LIFECYCLE_HOOKS',
                'methods',
                'head',
                ['template', 'render'],
                'renderError',
            ],
        }],
        '@typescript-eslint/no-explicit-any': 'off', // Allow usage of the any type
        '@typescript-eslint/explicit-module-boundary-types': 'off', // Do not require explicit return types
    },
};
